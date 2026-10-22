# 🚀 HireHub — Scalable Job Recruitment & ATS REST API

[![HireHub CI - Build & Test](https://github.com/izhar007ninja/hirehub-api/actions/workflows/ci.yml/badge.svg)](https://github.com/izhar007ninja/hirehub-api/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7.0-DC382D?style=flat&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?style=flat&logo=docker&logoColor=white)
![OpenAPI 3](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=flat&logo=swagger&logoColor=black)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)

**HireHub** is a production-grade, multi-role Job Recruitment and Applicant Tracking System (ATS) REST API built using **Java 21**, **Spring Boot 3**, and **MySQL**. It implements enterprise design patterns including clean layered architecture, stateless JWT role-based security, query optimization eliminating Hibernate N+1 overhead, sub-millisecond Redis caching, and automated CI/CD container pipelines.

---

## 📑 Table of Contents
- [Architecture & System Design](#-architecture--system-design)
- [Key Engineering Decisions](#-key-engineering-decisions)
- [Database Schema (ER Diagram)](#-database-schema-er-diagram)
- [Tech Stack](#-tech-stack)
- [API Catalog & Documentation](#-api-catalog--documentation)
- [Getting Started (Docker & Local Run)](#-getting-started)
- [Testing & Quality Assurance](#-testing--quality-assurance)

---

## 🏛 Architecture & System Design

HireHub enforces a clean separation of concerns, isolating controllers, business services, domain specifications, and persistence models: 

┌──────────────────────────┐
│   Client / Frontend UI   │
└─────────────┬────────────┘
│ (HTTPS / JSON)
▼
┌──────────────────────────┐
│  Delegated Auth Entry /  │
│ JwtAuthenticationFilter  │
└─────────────┬────────────┘
│
┌──────────────┴──────────────┐
▼                             ▼
┌─────────────────────┐       ┌─────────────────────┐
│ Public / Open Endpts│       │ Role Protected APIs │
│ (Jobs, Companies)   │       │ (Recruiter, Cand.)  │
└──────────┬──────────┘       └──────────┬──────────┘
│                             │
▼                             ▼
┌───────────────────────────────────────────────────┐
│                   Service Layer                   │
│  (JobService, ApplicationService, AdminService)   │
└──────────┬─────────────────────────────┬──────────┘
│                             │
(Cache Miss)     ▼                             ▼   (Fast Path / Hit)
┌─────────────────────┐       ┌─────────────────────┐
│   Spring Data JPA   │       │     Redis Cache     │
│   (Hibernate 6)     │       │   (TTL 5m-15m JSON) │
└──────────┬──────────┘       └─────────────────────┘
│
▼
┌─────────────────────┐
│    MySQL 8.0 DB     │
└─────────────────────┘

---

## 💡 Key Engineering Decisions

### 1. Hybrid Resume Snapshotting (Audit Compliance)
* **Problem:** If a candidate changes their master profile resume months after applying, recruiters reviewing past applications would inspect the mutated document rather than the document submitted at the time of evaluation.
* **Solution:** Candidates maintain a master resume in `candidate_profiles.resume_url` for one-click applications, but every submission immutably snapshots the active URL into `applications.resume_url`. This guarantees historical auditability without breaking Third Normal Form (3NF) requirements.

### 2. Elimination of Hibernate N+1 Query Overhead
* **Problem:** Fetching a paginated list of jobs caused Hibernate to generate 1 initial select followed by $N$ separate sub-queries for lazy-loaded `recruiter` and `skills` associations.
* **Solution:** Applied `@EntityGraph(attributePaths = {"recruiter", "skills"})` across queries in `JobRepository` and `ApplicationRepository`. Spring Data JPA compiles this directly to a single SQL `LEFT OUTER JOIN`, reducing query counts from $O(N)$ to $O(1)$.

### 3. Read-Heavy Cache Layering (Redis)
* Public job discovery endpoints (`/api/jobs`, `/api/jobs/{id}`) utilize `@Cacheable` with JSON serialization (`GenericJackson2JsonRedisSerializer`).
* Writes (`createJob`, `updateJob`, `deleteJob`, `forceCloseJob`) trigger selective eviction via `@CacheEvict(allEntries = true)` on search spaces to maintain cache coherency without stale reads.

### 4. Ownership Verification Pattern
* Role authorization is validated via method-level security (`@PreAuthorize("hasRole('RECRUITER')")`), but **resource ownership** is strictly verified at the service boundary (`application.getJob().getRecruiter().getId().equals(recruiter.getId())`). Recruiters cannot access or update applicants for postings owned by other recruiters.

---

## 🗄 Database Schema (ER Diagram)

The platform models 7 normalized relational tables with unique composite constraints enforcing business invariants (such as `uk_job_candidate` preventing duplicate applications):

```mermaid
erDiagram
    USERS ||--o| CANDIDATE_PROFILES : "has"
    USERS ||--o| RECRUITER_PROFILES : "has"
    USERS ||--o{ JOBS : "posts (as Recruiter)"
    USERS ||--o{ APPLICATIONS : "submits (as Candidate)"
    JOBS ||--o{ APPLICATIONS : "receives"
    JOBS }o--o{ SKILLS : "requires (job_skills)"
    CANDIDATE_PROFILES }o--o{ SKILLS : "possesses (candidate_skills)"

    USERS {
        bigint id PK
        varchar email UK
        varchar password
        varchar name
        varchar role
        boolean enabled
        timestamp created_at
    }

    JOBS {
        bigint id PK
        bigint recruiter_id FK
        varchar title
        text description
        varchar location
        varchar employment_type
        int experience_required
        decimal min_salary
        decimal max_salary
        varchar status
        timestamp created_at
    }

    APPLICATIONS {
        bigint id PK
        bigint job_id FK
        bigint candidate_id FK
        varchar status
        text cover_letter
        varchar resume_url
        timestamp applied_at
    }

    CANDIDATE_PROFILES {
        bigint id PK
        bigint user_id FK, UK
        varchar phone
        text bio
        varchar location
        int experience_years
        varchar resume_url
    }

    RECRUITER_PROFILES {
        bigint id PK
        bigint user_id FK, UK
        varchar company_name
        text company_description
        varchar company_website
        varchar location
    }

    SKILLS {
        bigint id PK
        varchar name UK
    }