-- Shopping initial partner products seed
-- This script is designed for manual execution because spring.sql.init.mode=never.
-- It is idempotent by purchase_url and product_id+skin_type checks.

INSERT INTO products (
    name, brand, category, image_url, purchase_url,
    price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at
)
SELECT
    '리얼 히알루로닉 100 토너', '웰라쥬', 'SKIN_TONER',
    'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000018512301ko.jpg',
    'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000185123',
    19800, 28000, 29, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000185123'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '레드 블레미쉬 클리어 수딩 토너', '닥터지', 'SKIN_TONER',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000016120101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000161201',
       23900, 32000, 25, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000161201'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '판테토인 에센스 토너', '마녀공장', 'SKIN_TONER',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000017320101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000173201',
       22400, 32000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000173201'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '티트리 시카 수딩 토너', '브링그린', 'SKIN_TONER',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000015030101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000150301',
       15900, 22000, 27, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000150301'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '어성초 흔적 에센스 패드', '아비브', 'SKIN_TONER',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000014210101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000142101',
       18900, 26000, 27, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000142101'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '어성초 카밍 토너 스킨 부스터', '아비브', 'SKIN_TONER',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000013990101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000139901',
       23200, 29000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000139901'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '비피다 바이옴 앰플 토너', '마녀공장', 'SKIN_TONER',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000013850101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138501',
       16200, 27000, 40, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138501'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '제로 모공 패드 2.0', '메디큐브', 'SKIN_TONER',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000012540101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000125401',
       18900, 27000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000125401'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '다이브인 저분자 히알루론산 세럼', '토리든', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000013580101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000135801',
       18000, 22000, 18, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000135801'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '리얼 히알루로닉 블루 100 앰플', '웰라쥬', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000014020101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000140201',
       21000, 30000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000140201'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '잡티케어 세럼', '아이소이', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000012340101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000123401',
       39000, 54000, 27, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000123401'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '아토베리어365 세라 히알 속수분 앰플', '에스트라', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000018910101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000189101',
       26400, 33000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000189101'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT 'PDRN 히알루론산 캡슐 100 세럼', '아누아', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000019500101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000195001',
       29000, 39000, 25, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000195001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '엔젤아쿠아 이온 히알루 10퍼센트 수분 진정 앰플', '비욘드', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000019230101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000192301',
       19600, 28000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000192301'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '마일드 리들샷 50', 'VT COSMETICS', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000018780101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000187801',
       21600, 27000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000187801'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '어성초 히알루론 수딩 앰플', '구달', 'ESSENCE_AMPOULE',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000018110101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181101',
       18200, 26000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181101'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '레드 블레미쉬 클리어 수딩 크림', '닥터지', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000011500101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000115001',
       28800, 38000, 24, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000115001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '아토베리어365 크림', '에스트라', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000012010101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000120101',
       27000, 31000, 12, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000120101'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '프로바이오덤 3D 리프팅 크림', '바이오힐보', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000015500101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000155001',
       31900, 40000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000155001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT 'DMT 페이셜 크림', '피지오겔', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000010120101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101201',
       23500, 30000, 21, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101201'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '세라마이드 아토 집중 크림', '일리윤', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000013210101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000132101',
       17500, 25000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000132101'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '시카플라스트 밤 B5+', '라로슈포제', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000017100101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000171001',
       29600, 37000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000171001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '리얼 히알루로닉 수딩크림', '웰라쥬', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000018300101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183001',
       21000, 30000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '모이스춰 닥터 크림 (장수진수분크림)', '아이소이', 'CREAM',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000014900101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000149001',
       28800, 36000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000149001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '티트리 에센셜 마스크', '메디힐', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000016200101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000162001',
       1000, 2000, 50, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000162001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '껌딱지 시트 마스크 어성초 스티커', '아비브', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000010500101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000105001',
       2000, 4000, 50, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000105001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '프리미엄 쿨 티트리 모델링 마스크', '린제이', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000011100101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000111001',
       2000, 3000, 33, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000111001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '리얼 히알루로닉 블루 앰플 마스크', '웰라쥬', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000014800101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000148001',
       1500, 3000, 50, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000148001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '마데카소사이드 에센셜 마스크', '메디힐', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000016300101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000163001',
       1000, 2000, 50, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000163001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT 'AC 딥 열감 진정 마스크', '듀이트리', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000015600101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000156001',
       1600, 3200, 50, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000156001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '참은만큼 보들보들 결세럼팩 [3번]', '넘버즈인', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000014500101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000145001',
       2000, 4000, 50, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000145001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '포스트 알파 퍼스트 쿨링 마스크', '셀퓨전씨', 'MASK_PACK',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000013100101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000131001',
       1500, 3000, 50, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000131001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '이너뷰티 저분자 콜라겐 비오틴 플러스', 'BB랩', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000015100101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000151001',
       19800, 29000, 31, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000151001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '뷰티 비타민 이너뷰티 글루타치온', '바이탈뷰티', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000018100101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181001',
       28000, 35000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '프리미엄 멀티비타민 샷', '오르토몰', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000013800101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138001',
       34000, 38000, 10, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '먹는 히알루론산 스킨 보습', '에스더포뮬러', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000017000101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000170001',
       24000, 32000, 25, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000170001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '액티브 비타민C 1000', '고려은단', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000010900101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000109001',
       12900, 15000, 14, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000109001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '엽산 800', '솔가', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000010100101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101001',
       25600, 32000, 20, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '달맞이꽃 종자유', '세노비스', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000011800101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000118001',
       29800, 38000, 21, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000118001'
);

INSERT INTO products (name, brand, category, image_url, purchase_url, price, original_price, discount_rate, price_updated_at, is_active, created_at, modified_at)
SELECT '멀티 액션 쿠텐', '블랙모어스', 'SUPPLEMENT',
       'https://image.oliveyoung.co.kr/uploads/images/goods/400/A00000012200101ko.jpg',
       'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000122001',
       24500, 35000, 30, NOW(), TRUE, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE purchase_url = 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000122001'
);

INSERT INTO product_skin_types (product_id, skin_type)
SELECT p.product_id, m.skin_type
FROM (
    SELECT MIN(product_id) AS product_id, purchase_url
    FROM products
    GROUP BY purchase_url
) p
JOIN (
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000185123' AS purchase_url, 'DRY' AS skin_type UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000185123', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000185123', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000185123', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000161201', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000161201', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000161201', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000173201', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000173201', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000173201', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000173201', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000150301', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000150301', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000150301', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000142101', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000142101', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000142101', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000139901', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000139901', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000139901', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000139901', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000139901', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138501', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138501', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138501', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138501', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000125401', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000125401', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000125401', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000135801', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000135801', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000135801', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000135801', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000140201', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000140201', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000140201', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000140201', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000123401', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000123401', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000123401', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000123401', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000189101', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000189101', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000189101', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000189101', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000195001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000195001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000195001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000195001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000192301', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000192301', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000192301', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000192301', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000192301', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000187801', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000187801', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000187801', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181101', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181101', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181101', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181101', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000115001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000115001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000115001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000120101', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000120101', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000120101', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000120101', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000155001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000155001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101201', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101201', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101201', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101201', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000132101', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000132101', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000132101', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000171001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000171001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000171001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000171001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000149001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000149001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000149001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000149001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000149001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000162001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000162001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000162001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000105001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000105001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000105001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000105001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000111001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000111001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000111001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000148001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000148001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000148001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000148001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000163001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000163001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000163001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000163001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000156001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000156001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000156001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000145001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000145001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000145001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000145001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000131001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000131001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000131001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000151001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000151001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000151001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000151001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000151001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000181001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000138001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000170001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000170001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000170001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000170001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000109001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000109001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000109001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000109001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000109001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000101001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000118001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000118001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000118001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000118001', 'NORMAL' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000122001', 'DRY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000122001', 'OILY' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000122001', 'COMBINATION' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000122001', 'DEHYDRATED' UNION ALL
    SELECT 'https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000122001', 'NORMAL'
) m ON m.purchase_url = p.purchase_url
WHERE NOT EXISTS (
    SELECT 1
    FROM product_skin_types pst
    WHERE pst.product_id = p.product_id
      AND pst.skin_type = m.skin_type
);
