-- 캠핑장 테이블 생성
CREATE TABLE IF NOT EXISTS campsite
(
    campsite_id
    BIGINT
    PRIMARY
    KEY,
    name
    VARCHAR
(
    255
) NOT NULL,
    management_type VARCHAR
(
    100
),
    province VARCHAR
(
    50
),
    city VARCHAR
(
    100
),
    address TEXT,
    road_address TEXT,
    zip_code VARCHAR
(
    20
),
    phone VARCHAR
(
    50
),
    homepage TEXT,

    -- 위치 정보 (POINT 타입으로 통합, NOT NULL for spatial index)
    location POINT NOT NULL,

    -- 사이트 정보
    general_camping BOOLEAN DEFAULT FALSE,
    auto_camping BOOLEAN DEFAULT FALSE,
    glamping BOOLEAN DEFAULT FALSE,
    caravan BOOLEAN DEFAULT FALSE,
    personal_caravan BOOLEAN DEFAULT FALSE,

    -- 사이트 크기 및 수량
    site_size1_width INT,
    site_size1_height INT,
    site_size1_count INT,
    site_size2_width INT,
    site_size2_height INT,
    site_size2_count INT,
    site_size3_width INT,
    site_size3_height INT,
    site_size3_count INT,

    -- 운영 정보
    weekday_open BOOLEAN DEFAULT FALSE,
    weekend_open BOOLEAN DEFAULT FALSE,
    spring_open BOOLEAN DEFAULT FALSE,
    summer_open BOOLEAN DEFAULT FALSE,
    fall_open BOOLEAN DEFAULT FALSE,
    winter_open BOOLEAN DEFAULT FALSE,

    -- 부대시설
    has_electricity BOOLEAN DEFAULT FALSE,
    has_hot_water BOOLEAN DEFAULT FALSE,
    has_wifi BOOLEAN DEFAULT FALSE,
    has_firewood BOOLEAN DEFAULT FALSE,
    has_walking_trail BOOLEAN DEFAULT FALSE,
    has_water_play BOOLEAN DEFAULT FALSE,
    has_playground BOOLEAN DEFAULT FALSE,
    has_mart BOOLEAN DEFAULT FALSE,
    has_firepit BOOLEAN DEFAULT FALSE,
    has_dump_station BOOLEAN DEFAULT FALSE,

    -- 시설 개수
    toilet_count INT,
    shower_count INT,
    sink_count INT,
    fire_extinguisher_count INT,
    fire_water_count INT,
    fire_sand_count INT,
    smoke_detector_count INT,

    -- 주변 시설
    nearby_fishing BOOLEAN DEFAULT FALSE,
    nearby_walking_trail BOOLEAN DEFAULT FALSE,
    nearby_beach BOOLEAN DEFAULT FALSE,
    nearby_water_sports BOOLEAN DEFAULT FALSE,
    nearby_valley BOOLEAN DEFAULT FALSE,
    nearby_river BOOLEAN DEFAULT FALSE,
    nearby_pool BOOLEAN DEFAULT FALSE,
    nearby_youth_facility BOOLEAN DEFAULT FALSE,
    nearby_rural_experience BOOLEAN DEFAULT FALSE,
    nearby_kids_playground BOOLEAN DEFAULT FALSE,

    -- 글램핑 시설
    glamping_bed BOOLEAN DEFAULT FALSE,
    glamping_tv BOOLEAN DEFAULT FALSE,
    glamping_fridge BOOLEAN DEFAULT FALSE,
    glamping_internet BOOLEAN DEFAULT FALSE,
    glamping_toilet BOOLEAN DEFAULT FALSE,
    glamping_aircon BOOLEAN DEFAULT FALSE,
    glamping_heater BOOLEAN DEFAULT FALSE,
    glamping_cooking BOOLEAN DEFAULT FALSE,

    -- 추가 정보
    theme TEXT,
    equipment_rental TEXT,
    pet_allowed BOOLEAN DEFAULT FALSE,
    facilities TEXT,
    nearby_facilities TEXT,
    features TEXT,
    introduction TEXT,
    license_date VARCHAR
(
    50
),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- 인덱스 생성
CREATE INDEX idx_campsite_province ON campsite (province);
CREATE INDEX idx_campsite_city ON campsite (province, city);
CREATE INDEX idx_campsite_name ON campsite (name);
CREATE INDEX idx_campsite_general_camping ON campsite (general_camping);
CREATE INDEX idx_campsite_auto_camping ON campsite (auto_camping);
CREATE INDEX idx_campsite_glamping ON campsite (glamping);
CREATE INDEX idx_campsite_caravan ON campsite (caravan);

-- Spatial 인덱스 생성 (location이 NOT NULL이므로 효율적)
CREATE
SPATIAL INDEX idx_campsite_spatial_location ON campsite(location);