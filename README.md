# Organaut - Organization Microservice

## Table of Contents
- [Overview](#overview)
- [Building and Deployment](#building-and-deployment)
- [Request Validation](#request-validation)
- [Security Architecture](#security-architecture)
- [Authentication](#authentication)
- [Server-Sent Events](#server-sent-events)
- [Data Persistence](#data-persistence)

## Overview

Organaut is a Spring Boot organization management microservice for the [Organizator](https://github.com/alldaygooning/organizator) project, handling organization data, addresses, coordinates, and real-time updates.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)
![JPA](https://img.shields.io/badge/Spring-Data-violet)
![Security](https://img.shields.io/badge/Spring-Security-red)
![Validation](https://img.shields.io/badge/Jakarta-Validation-purple)

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.7-blue)
![JWT](https://img.shields.io/badge/JWT-0.12.6-orange)
![Docker](https://img.shields.io/badge/Docker-28.1.1-lightblue)

## Building and Deployment

**Development Mode**: Runs in [Docker container](https://github.com/alldaygooning/organizator-organaut/blob/master/Dockerfile.dev) as part of [Docker Compose network](https://github.com/alldaygooning/organizator/blob/master/docker-compose.dev.yaml) with [HotSwapAgent](https://github.com/HotswapProjects/HotswapAgent) for runtime class reloading and JDWP debugging support on port 5005.

**Production Mode**:  Runs in [Docker container](https://github.com/alldaygooning/organizator-organaut/blob/master/Dockerfile.prod) as part of [Docker Compose network](https://github.com/alldaygooning/organizator/blob/master/docker-compose.prod.yaml) utilizing a standard Eclipse Temurin JDK 17 base image.

For comprehensive deployment instructions and environment configuration details, refer to the [Organizator Deployment Guide](https://github.com/alldaygooning/organizator).

## Request Validation

All incoming requests are validated using comprehensive Jakarta Bean Validation constraints. The validation framework enforces data integrity through:

- Null checks for required fields
- Size boundaries for string fields
- Minimum value constraints for numeric fields
- Custom validation messages with internationalization support
- Enum type validation for organization types

## Security Architecture

The service implements a dual-filter chain security model through Spring Security configuration:

**Public Chain**  
Handles read-only and public endpoints using regex-based URL matching for organization listings, address/coordinate data, and statistical endpoints without authentication requirements.

**Secure Chain**  
Protects mutation operations under `/api/organizations/**` requiring JWT authentication. This chain incorporates a custom JWT filter that validates tokens through the [LobbyBoy](https://github.com/alldaygooning/organizator-lobby_boy) microservice.

## Authentication

Authentication is delegated to the LobbyBoy microservice through JWT token validation:

- JWT tokens extracted from HTTP-only cookies
- Token validation via [LobbyBoy](https://github.com/alldaygooning/organizator-lobby_boy)
- Microservice-to-microservice communication secured with API keys
- User credentials propagation through security context
- Ownership-based authorization for organization operations

## Server-Sent Events

Real-time event streaming for live data synchronization across clients. Broadcasts object lifecycle events (create/delete/update operations) with structured JSON payloads. Automatic connection management handles timeouts and error recovery.

## Data Persistence

**JPA Transactions & Data Management**

The service employs strategic transaction management (via `@Transactional` and other Spring Data JPA capabilities) with read-only optimization for queries and comprehensive transaction protection for data modifications. Entity relationships are carefully managed through JPA mappings, ensuring referential integrity while maintaining data consistency across organization, address, and coordinate entities.

**Database functions**

Some business logic is handled through native SQL queries and stored `plpgsql` functions for optimal performance. The system includes analytical capabilities including statistical aggregations, ranking operations, and grouping queries that leverage database-level computations for efficiency.

**Data Integrity & Optimization**

A proactive duplicate prevention system ensures data uniqueness at the business layer before database insertion. System avoids creation and storing of identical objects (addresses, coordinates) seemlessly for clients.