package com.likelion.staycare.domain.mission.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.staycare.domain.mission.dto.openai.OpenAiResponsesRequest;
import com.likelion.staycare.domain.mission.dto.openai.OpenAiResponsesResponse;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningMissionRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.exception.OpenAiErrorCode;
import com.likelion.staycare.domain.mission.exception.OpenAiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private static final String RESPONSES_URI = "/responses";
    private static final String JSON_SCHEMA_TYPE = "json_schema";
    private static final String INPUT_TEXT_TYPE = "input_text";
    private static final String OUTPUT_TEXT_TYPE = "output_text";

    @Qualifier("openAiWebClient")
    private final WebClient openAiWebClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.model}")
    private String model;

    public MorningMissionResponse generateMorningMission(MorningMissionRequest request) {
        String prompt = buildMorningPrompt(request);
        OpenAiResponsesResponse response = requestMission(prompt);
        MissionPayload payload = parseMissionPayload(response);

        return MorningMissionResponse.builder()
                .title(payload.title())
                .description(payload.description())
                .steps(payload.steps())
                .tip(payload.tip())
                .build();
    }

    public EveningMissionResponse generateEveningMission(EveningMissionRequest request) {
        String prompt = buildEveningPrompt(request);
        OpenAiResponsesResponse response = requestMission(prompt);
        MissionPayload payload = parseMissionPayload(response);

        return EveningMissionResponse.builder()
                .title(payload.title())
                .description(payload.description())
                .steps(payload.steps())
                .tip(payload.tip())
                .build();
    }

    private OpenAiResponsesResponse requestMission(String userPrompt) {
        OpenAiResponsesRequest request = buildResponsesRequest(userPrompt);
        String responseJson = callResponsesApi(request);

        try {
            OpenAiResponsesResponse response = objectMapper.readValue(
                    responseJson,
                    OpenAiResponsesResponse.class
            );

            if (response.error() != null) {
                throw new OpenAiException(
                        OpenAiErrorCode.OPENAI_HTTP_ERROR,
                        response.error().message()
                );
            }

            return response;
        } catch (JsonProcessingException e) {
            log.error("OpenAI response JSON parse failed. response={}", responseJson, e);
            throw new OpenAiException(
                    OpenAiErrorCode.OPENAI_JSON_PARSE_ERROR,
                    e.getOriginalMessage()
            );
        }
    }

    private OpenAiResponsesRequest buildResponsesRequest(String userPrompt) {
        return new OpenAiResponsesRequest(
                model,
                List.of(
                        new OpenAiResponsesRequest.InputMessage(
                                "system",
                                List.of(new OpenAiResponsesRequest.InputContent(
                                        INPUT_TEXT_TYPE,
                                        buildSystemPrompt()
                                ))
                        ),
                        new OpenAiResponsesRequest.InputMessage(
                                "user",
                                List.of(new OpenAiResponsesRequest.InputContent(
                                        INPUT_TEXT_TYPE,
                                        userPrompt
                                ))
                        )
                ),
                new OpenAiResponsesRequest.TextConfig(
                        new OpenAiResponsesRequest.FormatConfig(
                                JSON_SCHEMA_TYPE,
                                "mission_response",
                                "Personalized skincare mission response",
                                buildMissionJsonSchema(),
                                true
                        )
                )
        );
    }

    private String callResponsesApi(OpenAiResponsesRequest request) {
        try {
            String responseJson = openAiWebClient.post()
                    .uri(RESPONSES_URI)
                    .bodyValue(request)
                    .exchangeToMono(clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .defaultIfEmpty("")
                                    .flatMap(body -> {
                                        if (clientResponse.statusCode().is2xxSuccessful()) {
                                            return Mono.just(body);
                                        }
                                        return Mono.error(createHttpException(clientResponse.statusCode(), body));
                                    }))
                    .block();

            if (responseJson == null || responseJson.isBlank()) {
                throw new OpenAiException(OpenAiErrorCode.OPENAI_EMPTY_RESPONSE);
            }

            return responseJson;
        } catch (OpenAiException e) {
            throw e;
        } catch (WebClientRequestException e) {
            log.error("OpenAI API request failed", e);
            throw new OpenAiException(OpenAiErrorCode.OPENAI_REQUEST_FAILED, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected OpenAI API error", e);
            throw new OpenAiException(OpenAiErrorCode.OPENAI_REQUEST_FAILED, e.getMessage());
        }
    }

    private OpenAiException createHttpException(HttpStatusCode statusCode, String responseBody) {
        log.error("OpenAI API error - status={}, body={}", statusCode.value(), responseBody);

        try {
            OpenAiResponsesResponse response = objectMapper.readValue(
                    responseBody,
                    OpenAiResponsesResponse.class
            );

            if (response.error() != null && response.error().message() != null) {
                return new OpenAiException(
                        OpenAiErrorCode.OPENAI_HTTP_ERROR,
                        "[" + statusCode.value() + "] " + response.error().message()
                );
            }
        } catch (JsonProcessingException ignored) {
            log.warn("Failed to parse OpenAI error response: {}", responseBody);
        }

        return new OpenAiException(
                OpenAiErrorCode.OPENAI_HTTP_ERROR,
                "[" + statusCode.value() + "] " + responseBody
        );
    }

    private MissionPayload parseMissionPayload(OpenAiResponsesResponse response) {
        String rawJson = extractOutputText(response);

        try {
            MissionPayload payload = objectMapper.readValue(rawJson, MissionPayload.class);
            validateMissionPayload(payload);
            return payload;
        } catch (JsonProcessingException e) {
            log.error("AI mission JSON parse failed. rawJson={}", rawJson, e);
            throw new OpenAiException(
                    OpenAiErrorCode.OPENAI_JSON_PARSE_ERROR,
                    e.getOriginalMessage()
            );
        }
    }

    private String extractOutputText(OpenAiResponsesResponse response) {
        if (response.outputText() != null && !response.outputText().isBlank()) {
            return response.outputText();
        }

        if (response.output() == null || response.output().isEmpty()) {
            throw new OpenAiException(OpenAiErrorCode.OPENAI_INVALID_RESPONSE);
        }

        String outputText = response.output().stream()
                .filter(outputItem -> outputItem.content() != null)
                .flatMap(outputItem -> outputItem.content().stream())
                .filter(content -> OUTPUT_TEXT_TYPE.equals(content.type()) && content.text() != null)
                .map(OpenAiResponsesResponse.OutputContent::text)
                .collect(Collectors.joining());

        if (outputText.isBlank()) {
            throw new OpenAiException(OpenAiErrorCode.OPENAI_EMPTY_RESPONSE);
        }

        return outputText;
    }

    private void validateMissionPayload(MissionPayload payload) {
        if (payload == null
                || isBlank(payload.title())
                || isBlank(payload.description())
                || isBlank(payload.tip())
                || payload.steps() == null
                || payload.steps().size() != 3
                || payload.steps().stream().anyMatch(this::isBlank)) {
            throw new OpenAiException(OpenAiErrorCode.OPENAI_INVALID_RESPONSE);
        }
    }

    private String buildSystemPrompt() {
        return """
                너는 피부관리 전문 AI이다.

                사용자의 피부 타입, 피부 상태, 이전 미션 수행 여부, 목표(goal), 관리 주기(checkCycle)를 종합적으로 고려하여
                현실적이고 실천 가능한 피부관리 미션만 생성한다.

                반드시 아래 규칙을 따른다.
                - 반드시 한국어로만 작성한다.
                - 반드시 JSON만 출력한다.
                - Markdown을 출력하지 않는다.
                - 코드블록을 출력하지 않는다.
                - 설명 문장을 추가하지 않는다.
                - title, description, steps, tip만 출력한다.
                - 피부관리 미션(step)은 반드시 정확히 3개만 생성한다.
                - steps 배열에는 반드시 3개의 문자열만 포함한다.
                - 추가 step을 생성하지 않는다.
                - 일정 관련 미션은 생성하지 않는다.
                - 화장품 추천을 하지 않는다.
                - 의료행위, 치료행위, 의학적 진단을 하지 않는다.
                - 사용자의 목표(goal)와 관리 주기(checkCycle)를 반드시 고려한다.
                - goal이 촉촉한 피부라면 보습 중심 미션을 우선한다.
                - goal이 트러블 진정이라면 진정 중심 미션을 우선한다.
                - checkCycle이 짧거나 매일에 가깝다면 매일 수행 가능한 루틴을 제안한다.
                - checkCycle 주기가 길다면 그에 맞는 난이도의 루틴을 제안한다.
                - 오늘 실제로 수행 가능한 행동만 제안한다.
                - title은 한 줄로 작성한다.
                - description은 한두 문장으로 간단하게 작성한다.
                - tip은 오늘 실천하면 좋은 한 가지 조언만 작성한다.
                - 반드시 주어진 JSON Schema를 따른다.
                """;
    }

    private String buildMorningPrompt(MorningMissionRequest request) {
        return """
                [아침 미션]

                - 나이 : %s
                - 피부 타입 : %s
                - 사용자 목표(goal) : %s
                - 관리 주기(checkCycle) : %s
                - 전날 저녁 피부 상태 : %s
                - 전날 저녁 미션 : %s
                - 전날 미션 수행 결과 : %s

                전날 데이터가 존재하지 않으면 "없음"으로 간주한다.
                첫 가입 사용자라면 전날 피부 상태와 전날 미션 수행 결과를 "없음"으로 처리하고 피부 타입, goal, checkCycle을 중심으로 미션을 생성한다.

                피부관리 미션(step)은 반드시 정확히 3개만 생성한다.
                steps 배열에는 반드시 3개의 문자열만 포함한다.
                추가 step을 생성하지 않는다.
                일정 관련 미션은 생성하지 않는다.
                """.formatted(
                defaultValue(request.age()),
                defaultValue(request.skinType()),
                defaultValue(request.goal()),
                defaultValue(request.checkCycle()),
                defaultValue(request.previousSkinCondition()),
                defaultValue(request.previousEveningMission()),
                defaultValue(request.previousEveningMissionResult())
        );
    }

    private String buildEveningPrompt(EveningMissionRequest request) {
        return """
                [저녁 미션]

                - 나이 : %s
                - 피부 타입 : %s
                - 사용자 목표(goal) : %s
                - 관리 주기(checkCycle) : %s
                - 오늘 피부 상태 : %s

                오늘 입력한 피부 상태를 반드시 반영한다.
                goal과 checkCycle은 참고 정보로 활용하되, 오늘 실제로 수행 가능한 피부관리 미션만 제안한다.

                피부관리 미션(step)은 반드시 정확히 3개만 생성한다.
                steps 배열에는 반드시 3개의 문자열만 포함한다.
                추가 step을 생성하지 않는다.
                일정 관련 미션은 생성하지 않는다.
                """.formatted(
                defaultValue(request.age()),
                defaultValue(request.skinType()),
                defaultValue(request.goal()),
                defaultValue(request.checkCycle()),
                defaultValue(request.todaySkinCondition())
        );
    }

    private Map<String, Object> buildMissionJsonSchema() {
        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "required", List.of("title", "description", "steps", "tip"),
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "description", Map.of("type", "string"),
                        "steps", Map.of(
                                "type", "array",
                                "minItems", 3,
                                "maxItems", 3,
                                "items", Map.of("type", "string")
                        ),
                        "tip", Map.of("type", "string")
                )
        );
    }

    private String defaultValue(String value) {
        return value == null || value.isBlank() ? "없음" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record MissionPayload(
            String title,
            String description,
            List<String> steps,
            String tip
    ) {
    }
}
