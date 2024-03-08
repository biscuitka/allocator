DROP TABLE IF EXISTS districts CASCADE;
DROP TABLE IF EXISTS resources CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS applications CASCADE;
DROP TABLE IF EXISTS resource_requests CASCADE;
DROP TABLE IF EXISTS resource_quotas CASCADE;

CREATE TABLE IF NOT EXISTS districts
(
    district_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    district_name  VARCHAR NOT NULL,
    CONSTRAINT unique_district_name UNIQUE (district_name)
    );

CREATE TABLE IF NOT EXISTS resources
(
    resource_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    resource_name VARCHAR NOT NULL,
    CONSTRAINT unique_resource_name UNIQUE (resource_name)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_name  VARCHAR NOT NULL,
    ticket_series BIGINT NOT NULL,
    ticket_number BIGINT NOT NULL,
    ticket_issue_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS applications
(
    application_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_type VARCHAR NOT NULL,
    application_status VARCHAR NOT NULL,
    application_created  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    applicant_id BIGINT NOT NULL,
    CONSTRAINT fk_applications_to_users FOREIGN KEY (applicant_id) REFERENCES users (user_id)
    );

CREATE TABLE IF NOT EXISTS resource_requests
(
    resource_request_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    resource_request_status VARCHAR NOT NULL,
    resource_request_quantity BIGINT NOT NULL,
    resource_id  BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    application_id BIGINT NOT NULL,
    CONSTRAINT fk_resource_requests_to_districts FOREIGN KEY (district_id) REFERENCES districts (district_id),
    CONSTRAINT fk_resource_requests_to_resource_names FOREIGN KEY (resource_id) REFERENCES resources (resource_id),
    CONSTRAINT fk_resource_requests_to_applications FOREIGN KEY (application_id) REFERENCES applications (application_id)
    );

CREATE TABLE IF NOT EXISTS resource_quotas
(
    resource_quota_id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    resource_quota_quantity BIGINT NOT NULL,
    resource_quota_start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    resource_quota_end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    resource_id  BIGINT NOT NULL,
    district_id BIGINT NOT NULL,
    CONSTRAINT fk_resource_quotas_to_districts FOREIGN KEY (district_id) REFERENCES districts (district_id),
    CONSTRAINT fk_resource_quotas_to_resources FOREIGN KEY (resource_id) REFERENCES resources (resource_id),
    CONSTRAINT unique_resource_id_district_id UNIQUE (resource_id, district_id)
    );





