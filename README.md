## User Service – ERD Diagram

```mermaid
erDiagram
    USER {
        BIGINT id PK
        VARCHAR email
        VARCHAR password
        VARCHAR first_name
        VARCHAR last_name
        BOOLEAN enabled
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    ROLE {
        BIGINT id PK
        VARCHAR name
    }

    USER_ROLE {
        BIGINT user_id FK
        BIGINT role_id FK
    }

    PATIENT {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR first_name
        VARCHAR last_name
        DATE date_of_birth
        VARCHAR gender
        VARCHAR phone
        VARCHAR address
    }

    DOCTOR {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR specialization
        VARCHAR license_number
        INT experience_years
        VARCHAR department
    }

    JWT_TOKEN {
        STRING token
        TIMESTAMP expires_at
        STRING subject
    }

    USER ||--o{ USER_ROLE : has_roles
    ROLE ||--o{ USER_ROLE : assigned_to

    USER ||--|| PATIENT : patient_info
    USER ||--|| DOCTOR  : doctor_info

    USER ||--o{ JWT_TOKEN : generates
