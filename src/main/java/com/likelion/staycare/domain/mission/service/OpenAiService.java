package com.likelion.staycare.domain.mission.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.staycare.domain.mission.dto.openai.OpenAiResponsesRequest;
import com.likelion.staycare.domain.mission.dto.openai.OpenAiResponsesResponse;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
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

    public List<String> recommendMorningRoutine(
            String age,
            String skinType,
            String goal,
            String checkCycle,
            List<MorningMissionCategory> categories,
            List<String> existingRoutines,
            int recommendationCount
    ) {
        String prompt = buildMorningRoutineRecommendationPrompt(
                age,
                skinType,
                goal,
                checkCycle,
                categories,
                existingRoutines,
                recommendationCount
        );
        OpenAiResponsesResponse response = requestStructuredResponse(
                prompt,
                "morning_routine_recommendations",
                "Morning routine recommendation response",
                buildRecommendationJsonSchema(recommendationCount)
        );
        RecommendationPayload payload = parseRecommendationPayload(response);

        if (payload.recommendations() == null || payload.recommendations().size() != recommendationCount) {
            throw new OpenAiException(OpenAiErrorCode.OPENAI_INVALID_RESPONSE);
        }

        return payload.recommendations();
    }

    public EveningMissionResponse generateEveningMission(EveningMissionRequest request) {
        String prompt = buildEveningPrompt(request);
        OpenAiResponsesResponse response = requestStructuredResponse(
                prompt,
                "mission_response",
                "Personalized evening wellness mission response",
                buildMissionJsonSchema()
        );
        MissionPayload payload = parseMissionPayload(response);

        return EveningMissionResponse.builder()
                .title(payload.title())
                .description(payload.description())
                .stepIds(List.of())
                .steps(payload.steps())
                .build();
    }

    private OpenAiResponsesResponse requestStructuredResponse(
            String userPrompt,
            String schemaName,
            String schemaDescription,
            Map<String, Object> schema
    ) {
        OpenAiResponsesRequest request = new OpenAiResponsesRequest(
                model,
                List.of(
                        new OpenAiResponsesRequest.InputMessage(
                                "system",
                                List.of(new OpenAiResponsesRequest.InputContent(INPUT_TEXT_TYPE, buildSystemPrompt()))
                        ),
                        new OpenAiResponsesRequest.InputMessage(
                                "user",
                                List.of(new OpenAiResponsesRequest.InputContent(INPUT_TEXT_TYPE, userPrompt))
                        )
                ),
                new OpenAiResponsesRequest.TextConfig(
                        new OpenAiResponsesRequest.FormatConfig(
                                JSON_SCHEMA_TYPE,
                                schemaName,
                                schemaDescription,
                                schema,
                                true
                        )
                )
        );

        String responseJson = callResponsesApi(request);

        try {
            OpenAiResponsesResponse response = objectMapper.readValue(responseJson, OpenAiResponsesResponse.class);

            if (response.error() != null) {
                throw new OpenAiException(OpenAiErrorCode.OPENAI_HTTP_ERROR, response.error().message());
            }

            return response;
        } catch (JsonProcessingException e) {
            log.error("OpenAI response JSON parse failed. response={}", responseJson, e);
            throw new OpenAiException(OpenAiErrorCode.OPENAI_JSON_PARSE_ERROR, e.getOriginalMessage());
        }
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
            OpenAiResponsesResponse response = objectMapper.readValue(responseBody, OpenAiResponsesResponse.class);
            if (response.error() != null && response.error().message() != null) {
                return new OpenAiException(
                        OpenAiErrorCode.OPENAI_HTTP_ERROR,
                        "[" + statusCode.value() + "] " + response.error().message()
                );
            }
        } catch (JsonProcessingException ignored) {
            log.warn("Failed to parse OpenAI error response: {}", responseBody);
        }

        return new OpenAiException(OpenAiErrorCode.OPENAI_HTTP_ERROR, "[" + statusCode.value() + "] " + responseBody);
    }

    private MissionPayload parseMissionPayload(OpenAiResponsesResponse response) {
        String rawJson = extractOutputText(response);

        try {
            MissionPayload payload = objectMapper.readValue(rawJson, MissionPayload.class);
            if (payload == null
                    || isBlank(payload.title())
                    || isBlank(payload.description())
                    || payload.steps() == null
                    || payload.steps().size() != 3
                    || payload.steps().stream().anyMatch(this::isBlank)) {
                throw new OpenAiException(OpenAiErrorCode.OPENAI_INVALID_RESPONSE);
            }
            return payload;
        } catch (JsonProcessingException e) {
            log.error("AI mission JSON parse failed. rawJson={}", rawJson, e);
            throw new OpenAiException(OpenAiErrorCode.OPENAI_JSON_PARSE_ERROR, e.getOriginalMessage());
        }
    }

    private RecommendationPayload parseRecommendationPayload(OpenAiResponsesResponse response) {
        String rawJson = extractOutputText(response);

        try {
            RecommendationPayload payload = objectMapper.readValue(rawJson, RecommendationPayload.class);
            if (payload == null || payload.recommendations() == null || payload.recommendations().stream().anyMatch(this::isBlank)) {
                throw new OpenAiException(OpenAiErrorCode.OPENAI_INVALID_RESPONSE);
            }
            return payload;
        } catch (JsonProcessingException e) {
            log.error("AI recommendation JSON parse failed. rawJson={}", rawJson, e);
            throw new OpenAiException(OpenAiErrorCode.OPENAI_JSON_PARSE_ERROR, e.getOriginalMessage());
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

    private String buildSystemPrompt() {
        return """
                당신은 개인 맞춤 스킨케어 + 생활습관 + 웰니스 루틴 코치입니다.

                사용자의 피부 타입, 진단 결과, 루틴 수행 여부, 귀가 후 상태, 일정 맥락을 함께 고려해
                오늘 실제로 수행 가능한 피부관리 및 웰니스 미션을 추천합니다.

                반드시 다음 규칙을 지킵니다.
                - 한국어로만 작성합니다.
                - JSON만 출력합니다.
                - Markdown, 코드블록, 설명 문장은 출력하지 않습니다.
                - 의료 진단, 질병 치료 단정, 처방 제안은 하지 않습니다.
                - 일반적인 피부관리, 생활습관, 웰니스 수준의 실행 가능한 행동만 제안합니다.
                - 일정 자체를 수행하라는 미션은 생성하지 않습니다.
                - 저녁 미션 step은 정확히 3개만 생성합니다.
                - 아침 루틴 추천은 매일 반복 가능한 고정 루틴 위주로 추천합니다.
                - 이미 유지 중인 루틴과 중복되는 추천은 피합니다.
                """;
    }

    private String buildMorningRoutineRecommendationPrompt(
            String age,
            String skinType,
            String goal,
            String checkCycle,
            List<MorningMissionCategory> categories,
            List<String> existingRoutines,
            int recommendationCount
    ) {
        String categoryText = categories == null || categories.isEmpty()
                ? "없음"
                : categories.stream().map(MorningMissionCategory::getLabel).collect(Collectors.joining(", "));

        return """
                [고정 아침 루틴 추천]

                - 나이: %s
                - 피부 타입: %s
                - 목표(goal): %s
                - 관리 주기(checkCycle): %s
                - 원하는 카테고리: %s
                - 현재 유지 중인 루틴: %s
                - 필요한 추천 개수: %d개

                매일 반복 가능한 고정 아침 루틴 후보를 추천합니다.
                피부관리뿐 아니라 위생, 수분, 식습관, 생활습관 수준의 아침 루틴도 포함할 수 있습니다.
                현재 유지 중인 루틴과 중복되는 표현은 피합니다.
                recommendations 배열에는 정확히 %d개의 문자열만 넣습니다.
                각 항목은 짧고 바로 실천 가능한 문장으로 작성합니다.
                """.formatted(
                defaultValue(age),
                defaultValue(skinType),
                defaultValue(goal),
                defaultValue(checkCycle),
                categoryText,
                existingRoutines == null || existingRoutines.isEmpty() ? "없음" : String.join(", ", existingRoutines),
                recommendationCount,
                recommendationCount
        );
    }

    private String buildEveningPrompt(EveningMissionRequest request) {
        return """
                [저녁 미션 생성]

                - 나이: %s
                - 피부 타입: %s
                - 목표(goal): %s
                - 관리 주기(checkCycle): %s
                - 오늘 일정: %s
                - 귀가 후 상태: %s
                - 오늘 아침 미션 상태: %s
                - 피부 타입 참고: %s

                사용자가 오늘 저녁에 실제로 할 수 있는 피부관리/웰니스 미션을 추천합니다.
                아침에 완료한 루틴은 과도하게 반복하지 말고, 미완료 항목이나 오늘 컨디션에 맞춰 보완합니다.
                오늘 일정 맥락이 있다면 외부활동, 이동, 피로, 건조, 자극 가능성을 자연스럽게 고려합니다.
                steps 배열에는 정확히 3개의 문자열만 넣습니다.
                일정 수행 자체를 step으로 만들지 않습니다.
                """.formatted(
                defaultValue(request.age()),
                defaultValue(request.skinType()),
                defaultValue(request.goal()),
                defaultValue(request.checkCycle()),
                defaultValue(request.todaySchedule()),
                defaultValue(request.todayConditions()),
                defaultValue(request.morningMissionStatus()),
                describeSkinType(request.skinType())
        );
    }

    private Map<String, Object> buildMissionJsonSchema() {
        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "required", List.of("title", "description", "steps"),
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "description", Map.of("type", "string"),
                        "steps", Map.of(
                                "type", "array",
                                "minItems", 3,
                                "maxItems", 3,
                                "items", Map.of("type", "string")
                        )
                )
        );
    }

    private Map<String, Object> buildRecommendationJsonSchema(int recommendationCount) {
        return Map.of(
                "type", "object",
                "additionalProperties", false,
                "required", List.of("recommendations"),
                "properties", Map.of(
                        "recommendations", Map.of(
                                "type", "array",
                                "minItems", recommendationCount,
                                "maxItems", recommendationCount,
                                "items", Map.of("type", "string")
                        )
                )
        );
    }

    private String describeSkinType(String skinType) {
        if (skinType == null || skinType.isBlank()) {
            return "없음";
        }

        return switch (skinType) {
            case "건성", "DRY" -> "건조함과 보습 관리 중심";
            case "지성", "OILY" -> "과도한 유분과 산뜻한 관리 중심";
            case "복합성", "COMBINATION" -> "부위별 유수분 균형 관리 중심";
            case "수부지", "DEHYDRATED" -> "겉 유분과 속 수분 부족을 함께 고려한 유수분 균형 관리 중심";
            case "중성", "NORMAL" -> "기본적인 피부 컨디션 유지 중심";
            default -> skinType;
        };
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
            List<String> steps
    ) {
    }

    private record RecommendationPayload(
            List<String> recommendations
    ) {
    }
}
