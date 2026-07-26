# **Production-Grade Architecture for Restaurant Verification and Onboarding in Food Delivery Platforms**

The rapid expansion of the quick-commerce and food delivery sectors has necessitated highly automated, deterministic, and scalable merchant onboarding systems. Leading aggregators operating in the Indian market, such as Swiggy and Zomato, deploy rigorous documentary and identity check frameworks prior to activating any restaurant on their platforms. This stringent vetting ensures compliance with federal tax mandates, state municipal laws, and food safety regulations, while simultaneously mitigating financial fraud through algorithmic identity matching. A manual onboarding pipeline introduces severe operational bottlenecks and human error, necessitating a zero-touch, API-driven backend architecture.  
This report provides an exhaustive, blueprint-level analysis of the restaurant verification lifecycle. It deconstructs the legal, financial, and geospatial prerequisites for merchant onboarding, establishes a hierarchical data model, and supplies a comprehensive, step-by-step Java (Spring Boot 3\) implementation guide suitable for autonomous software agents to execute a production-ready system.

## **The Regulatory Landscape and Documentary Prerequisites**

To legally operate as a food business aggregator, a platform must collect and instantly verify specific governmental documents. The onboarding flow requires merchants to upload high-resolution images alongside exact identification numbers, which are subsequently routed through external regulatory APIs for real-time validation. The documentation is tiered to establish the legal identity of the business, its tax compliance status, and its adherence to health standards.  
The Food Safety and Standards Authority of India (FSSAI) mandates that any business preparing, packing, or selling food must possess an active license to ensure baseline hygiene1. Food delivery platforms cannot legally onboard a restaurant or cloud kitchen without verifying their 14-digit FSSAI number2. The specific tier of the FSSAI license depends on the merchant's operational scale; businesses with an annual turnover below ₹12 Lakhs require only a Basic Registration, whereas higher-volume operators must obtain a State or Central License1. The aggregator backend must validate this 14-digit number against national registries to confirm its active status and ensure the registered premise matches the physical location pinned on the platform.  
Concurrently, the platform must verify the merchant's financial and tax identity. The Permanent Account Number (PAN) serves as the foundational identity document for the business entity or individual proprietor, utilized to establish legal identity and process direct tax deductions at source1. Furthermore, e-commerce operators are mandated to collect Goods and Services Tax (GST) on restaurant services. Therefore, restaurants must provide a valid Goods and Services Tax Identification Number (GSTIN) to be listed and to legally receive financial payouts1. While GST registration is technically mandatory only for businesses with an annual turnover exceeding ₹40 Lakhs, major aggregators strongly recommend or mandate it for all partners to facilitate seamless input tax credit flows and regulatory compliance1.  
For the physical premise, a Shop and Establishment Act Certificate, often referred to as a Trade License, is required by municipal authorities to grant permission to operate a commercial establishment in a specific locality1. Finally, to route financial settlements, the platform must collect Bank Account Proof in the form of a cancelled cheque or a bank passbook1. This document must explicitly display the business name, account number, and the Indian Financial System Code (IFSC) to facilitate automated payouts2.

| Verification Domain | Required Document | Primary Authority | Verification API Providers | Purpose in Architecture |
| :---- | :---- | :---- | :---- | :---- |
| **Tax & Legal Identity** | PAN & GSTIN | Income Tax Dept / GSTN | Karza, Signzy, Surepass | Validates entity existence, fetches legal corporate name, and ensures indirect tax compliance. |
| **Food Safety Compliance** | 14-Digit License | FSSAI | Karza, Signzy, IDfy | Certifies hygiene standards and links the legal entity to a specific physical kitchen premise. |
| **Financial Routing** | Cancelled Cheque / IFSC | RBI / NPCI | Cashfree, RazorpayX, Signzy | Extracts banking coordinates for routing daily or weekly earnings settlements. |
| **Geospatial Proximity** | PostGIS Coordinates | Local Mapping Data | Ola Maps, Google Maps | Determines serviceable delivery radius, calculates ETAs, and routes delivery executives. |

## **Image and Asset Standardization Protocols**

Beyond legal documents, the onboarding system must ingest, validate, and store digital assets that represent the merchant to the consumer. The visual presentation of a restaurant directly influences conversion rates, necessitating strict algorithmic validations on uploaded imagery before a restaurant can go live3.  
Platforms enforce rigorous specifications for menu and hero images. For instance, Swiggy mandates that uploaded images must maintain a high resolution, preferring 300 DPI, though a minimum of 100 DPI is permissible3. The structural dimensions of these digital assets are heavily regulated; hero images must conform to a width of 2800 pixels and a height of 2519 pixels, resulting in an exact 10:9 aspect ratio3. Furthermore, the system must enforce a 170-pixel padding on all four sides to ensure that UI elements, such as discount tags or delivery time overlays, do not obscure the primary subject of the photograph3.  
Thumbnail images require a distinct 11:13 aspect ratio, precisely scaled to 1760 by 2080 pixels, and algorithms must verify the absence of superimposed logos, promotional text, or watermarks3. Only standard raster formats, specifically JPG, are accepted through the intake pipeline3. The backend architecture must integrate image processing libraries (such as ImageMagick or native Java 2D APIs) during the upload stream to synchronously validate these dimensions, ratios, and file formats, rejecting non-compliant payloads before they consume expensive cloud storage resources.

## **Architectural Paradigm: Hierarchical Data Modeling**

Handling multi-outlet restaurants, such as national fast-food chains or sprawling cloud kitchen franchises, mandates a highly normalized, hierarchical database schema. Treating a fifty-location franchise identically to a single standalone cafe creates catastrophic failures in legal compliance, payout routing, and geospatial delivery logistics7. The architecture must strictly segregate the overarching brand from its individual physical kitchens.  
The parent entity in this hierarchy is the Brand. The Brand table stores the corporate legal identity, meaning that tax compliance, corporate KYC, and payout settlements are managed exclusively at this tier7. The parent record holds the GSTIN and PAN documents and maintains the centrally verified bank account details for financial disbursements7. This design ensures that a corporate entity undergoes the heavy, paid API verification cycles (such as GSTIN validation and bank penny drops) exactly once, regardless of how many child outlets they subsequently open.  
The child entity in the hierarchy is the Outlet, which represents the physical storefront or cloud kitchen tied to the parent Brand. The Outlet table holds the unique 14-digit FSSAI license, as food safety is evaluated and licensed strictly on a per-kitchen, per-premise basis7. Furthermore, the child entity stores the static structural location of the restaurant using geospatial coordinates. This separation ensures that a single brand can have dozens of outlets spread across a city, each with its own specific operational hours, FSSAI compliance status, and PostGIS location for localized dispatching7.

## **PostGIS and Geospatial System Design**

The static location of every outlet must be captured with extreme precision to facilitate turn-by-turn routing and accurate Estimated Time of Arrival (ETA) calculations. Relying on text-based addresses introduces severe geocoding latency and inaccuracies during real-time dispatch7. The backend architecture utilizes the PostGIS extension for PostgreSQL to natively store and query these locations as mathematical geometries8.  
The database schema defines the location column using the geometry(Point, 4326\) data type9. The integer 4326 refers to the Spatial Reference System Identifier (SRID) for the World Geodetic System 1984 (WGS 84), which is the standard coordinate frame used by GPS satellites, Google Maps, and Ola Maps8. By explicitly defining the SRID at the schema level, the database ensures that all incoming latitude and longitude pairs are correctly mapped to the earth's curvature, preventing critical errors in distance calculations8.  
When a customer opens the application, the system must instantly query the database to find all active outlets within a specific radius (e.g., 5 kilometers). PostGIS enables this through the ST\_DWithin function, which calculates whether two geometries are within a specified distance of one another8. To prevent the database from executing a full-table scan for every customer request, the architecture dictates the creation of a Generalized Search Tree (GiST) spatial index on the location column9. The GiST index utilizes bounding boxes to rapidly eliminate distant geometries, allowing the system to serve proximity queries with sub-millisecond latency even when querying millions of registered outlets10.

## **Algorithmic Identity Matching: Jaro-Winkler Similarity**

A critical vulnerability in automated onboarding is the identity mismatch between legal tax documents and banking records. When the platform executes a bank verification, the banking network returns the registered beneficiary name7. This name must be programmatically compared against the legal entity name fetched from the GSTIN or PAN verification7.  
Exact string matching will universally fail due to arbitrary abbreviations, typographical errors, and structural variations (e.g., matching "KENTUCKY FRIED CHICKEN PVT LTD" against "KFC PRIVATE LIMITED"). While the Levenshtein distance algorithm calculates the minimum single-character edits required to mutate one string into another, it is computationally expensive and treats all character edits equally12. For corporate names and human identities, errors are far more likely to occur at the end of a string (due to trailing abbreviations like "LTD" or "LLC") rather than at the beginning12.  
To solve this, the architecture implements the Jaro-Winkler distance algorithm, which is purposefully designed for record linkage and short string matching13. The algorithm produces a normalized score between 0.0 (indicating zero similarity) and 1.0 (indicating an exact, perfect match)14.  
The base Jaro similarity score (![][image1]) calculates a weighted sum based on the number of matching characters (![][image2]) and the number of transpositions (![][image3]) across the lengths of the two strings (![][image4] and ![][image5])14. The mathematical formulation is as follows:  
![][image6]  
Two characters are only considered a match if they are identical and located within a specific maximum distance of one another, calculated as ![][image7]14.  
The Winkler modification subsequently applies a scaling factor to the base Jaro score, providing a significant mathematical bonus to strings that share a common prefix12. This is critical for Indian corporate names, where the primary identifier is heavily weighted toward the beginning of the string. The Jaro-Winkler score (![][image8]) is calculated using the length of the common prefix (![][image9]), up to a maximum of 4 characters, multiplied by a standard scaling factor (![][image10]) of 0.112:  
![][image11]  
By integrating this algorithm, the platform can establish an automated confidence threshold (typically between 0.85 and 0.90). If the score exceeds this threshold, the system automatically approves the financial routing setup; if it falls below, the record is flagged in the database for manual compliance review12.

## **The API Integration Pipeline**

To achieve a zero-touch onboarding flow, the backend must orchestrate a sequence of synchronous and asynchronous API calls to certified Indian regulatory gateways. Providers such as Karza, Signzy, and Cashfree expose RESTful endpoints that allow the application to validate credentials in real-time.

### **Legal and Tax Verification Payload (Karza)**

The verification pipeline begins by validating the merchant's GSTIN. The backend constructs a JSON payload containing the merchant's consent flag and the 15-character GSTIN, which is transmitted via a secure HTTP POST request to the Karza API gateway16. The request must be authenticated using a proprietary API key passed in the headers.

| HTTP Element | Configuration Details |
| :---- | :---- |
| **Endpoint URL** | https://testapi.karza.in/v2/gst-verification \[cite: 16\] |
| **HTTP Method** | POST16 |
| **Headers** | Content-Type: application/json, x-karza-key: {{api-key}} \[cite: 16\] |
| **Request Body** | {"consent": "Y", "gstin": "27AAAXXXXX1Z7"} \[cite: 16\] |

The provider queries the GSTN network and returns a highly nested JSON response indicating the entity's active status, registered address, and exact legal name. This legal name is immediately persisted to the database for subsequent Jaro-Winkler matching. Similar endpoints are utilized for FSSAI verification and Legal Entity Identifier (LEI) checks17.

### **Financial Verification Payload (Penny Drop)**

Following tax verification, the system must ensure the merchant's bank account is active and belongs to the registered entity. This is achieved through an automated Immediate Payment Service (IMPS) transaction, colloquially known as a "Penny Drop," where ₹1.00 is deposited into the merchant's account18.  
Modern payment gateways like Cashfree utilize asynchronous webhooks for this process, often referred to as a Reverse Penny Drop19. The backend initiates the request by submitting the account number and IFSC20. Because banking networks can experience high latency, the API does not block the thread; instead, it returns an acknowledgment19. The backend must expose a secure, public-facing webhook listener. Once the NPCI processes the transaction, the gateway fires an asynchronous POST request to this webhook containing the penny-drop-request-details and the GetStatusRpdResponseSchema19. The backend parses this payload to extract the beneficiaryName and the final transaction status.

## **Step-by-Step Java Implementation Guide**

The following sections provide a complete, production-ready implementation blueprint utilizing Java 17+, Spring Boot 3, Hibernate/JPA, and Resilience4j. This code is structured to enforce the hierarchical data model, execute the geospatial PostGIS mappings, and securely interact with the external KYC APIs.

### **1\. Database Schema and DDL**

The underlying PostgreSQL schema establishes the relational constraints and spatial indexes required for high-performance geospatial queries and strict data integrity.

SQL  
\-- Enable PostGIS on current instance of PostgreSQL for spatial data support  
CREATE EXTENSION IF NOT EXISTS postgis;

\-- Brand Table: Centralizes legal and financial identity  
CREATE TABLE brands (  
    brand\_id BIGSERIAL PRIMARY KEY,  
    legal\_entity\_name VARCHAR(255) NOT NULL,  
    pan\_number VARCHAR(10) UNIQUE NOT NULL,  
    gstin VARCHAR(15) UNIQUE,  
    kyc\_status VARCHAR(50) DEFAULT 'PENDING',  
    bank\_account\_number VARCHAR(50),  
    bank\_ifsc\_code VARCHAR(20),  
    bank\_beneficiary\_name VARCHAR(255),  
    penny\_drop\_status VARCHAR(50) DEFAULT 'PENDING',  
    created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP  
);

\-- Outlet Table: Manages physical locations and FSSAI compliance  
CREATE TABLE outlets (  
    outlet\_id BIGSERIAL PRIMARY KEY,  
    brand\_id BIGINT NOT NULL,  
    outlet\_name VARCHAR(255) NOT NULL,  
    fssai\_license\_number VARCHAR(14) NOT NULL,  
    fssai\_status VARCHAR(50) DEFAULT 'PENDING',  
    contact\_phone VARCHAR(20),  
    \-- PostGIS Geometry column enforced to WGS84 (SRID 4326\) coordinates  
    location geometry(Point, 4326) NOT NULL,  
    is\_active BOOLEAN DEFAULT FALSE,  
    created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP,  
    CONSTRAINT fk\_brand FOREIGN KEY (brand\_id) REFERENCES brands(brand\_id) ON DELETE CASCADE  
);

\-- GiST Spatial Index: Crucial for sub-millisecond proximity routing  
CREATE INDEX idx\_outlets\_location ON outlets USING GIST (location);

\-- Audit Table: Immutably logs all API interactions for regulatory compliance  
CREATE TABLE verification\_audit\_logs (  
    log\_id BIGSERIAL PRIMARY KEY,  
    entity\_type VARCHAR(50) NOT NULL,  
    entity\_id BIGINT NOT NULL,  
    verification\_provider VARCHAR(100) NOT NULL,  
    raw\_request\_payload JSONB,  
    raw\_response\_payload JSONB,  
    similarity\_score NUMERIC(5,4),  
    status VARCHAR(50) NOT NULL,  
    created\_at TIMESTAMP DEFAULT CURRENT\_TIMESTAMP  
);

### **2\. Spring Boot Domain Models (JPA Entities)**

The domain layer maps the relational schema to Java objects. Integrating PostGIS with Hibernate requires importing the hibernate-spatial dependency and utilizing the org.locationtech.jts.geom.Point class to represent the mathematical geometry in memory.

Java  
package com.fooddelivery.onboarding.domain;

import jakarta.persistence.\*;  
import org.locationtech.jts.geom.Point;  
import lombok.Data;  
import java.time.LocalDateTime;

@Entity  
@Table(name \= "brands")  
@Data  
public class Brand {  
    @Id  
    @GeneratedValue(strategy \= GenerationType.IDENTITY)  
    private Long brandId;

    @Column(name \= "legal\_entity\_name", nullable \= false)  
    private String legalEntityName;  
      
    @Column(name \= "pan\_number", nullable \= false, unique \= true)  
    private String panNumber;  
      
    @Column(unique \= true)  
    private String gstin;  
      
    @Enumerated(EnumType.STRING)  
    @Column(name \= "kyc\_status")  
    private VerificationStatus kycStatus \= VerificationStatus.PENDING;  
      
    @Column(name \= "bank\_account\_number")  
    private String bankAccountNumber;  
      
    @Column(name \= "bank\_ifsc\_code")  
    private String bankIfscCode;  
      
    @Column(name \= "bank\_beneficiary\_name")  
    private String bankBeneficiaryName;

    @Enumerated(EnumType.STRING)  
    @Column(name \= "penny\_drop\_status")  
    private VerificationStatus pennyDropStatus \= VerificationStatus.PENDING;

    @Column(name \= "created\_at", updatable \= false)  
    private LocalDateTime createdAt \= LocalDateTime.now();  
}

@Entity  
@Table(name \= "outlets")  
@Data  
public class Outlet {  
    @Id  
    @GeneratedValue(strategy \= GenerationType.IDENTITY)  
    private Long outletId;

    @ManyToOne(fetch \= FetchType.LAZY)  
    @JoinColumn(name \= "brand\_id", nullable \= false)  
    private Brand brand;

    @Column(name \= "outlet\_name", nullable \= false)  
    private String outletName;  
      
    @Column(name \= "fssai\_license\_number", nullable \= false, length \= 14\)  
    private String fssaiLicenseNumber;

    @Enumerated(EnumType.STRING)  
    @Column(name \= "fssai\_status")  
    private VerificationStatus fssaiStatus \= VerificationStatus.PENDING;  
      
    // Explicitly defining the PostGIS definition for the JPA provider  
    @Column(columnDefinition \= "geometry(Point,4326)", nullable \= false)  
    private Point location;  
      
    @Column(name \= "is\_active")  
    private Boolean isActive \= false;  
}

public enum VerificationStatus {  
    PENDING, VERIFIED, REJECTED, FAILED  
}

### **3\. Jaro-Winkler Algorithm Implementation**

To evaluate identity matches without introducing massive, heavy dependencies like Apache Commons Text into the core domain layer, the architecture utilizes a highly optimized, allocation-free algorithmic implementation of the Jaro-Winkler metric. This logic directly implements the mathematical formulations detailed earlier.

Java  
package com.fooddelivery.onboarding.util;

public class JaroWinklerMatcher {

    /\*\*  
     \* Computes the Jaro-Winkler similarity score between two strings.  
     \* Returns a normalized score between 0.0 (no match) and 1.0 (exact match).  
     \*/  
    public static double computeSimilarity(String s1, String s2) {  
        if (s1 \== null || s2 \== null) return 0.0;  
        if (s1.equals(s2)) return 1.0;

        s1 \= s1.toUpperCase().trim();  
        s2 \= s2.toUpperCase().trim();

        int len1 \= s1.length();  
        int len2 \= s2.length();  
        if (len1 \== 0 || len2 \== 0) return 0.0;

        // Calculate maximum distance for character matching  
        int maxMatchDistance \= Math.max(0, Math.max(len1, len2) / 2 \- 1);  
        boolean\[\] s1Matches \= new boolean\[len1\];  
        boolean\[\] s2Matches \= new boolean\[len2\];  
          
        int matches \= 0;  
          
        // Phase 1: Count exact matches within the allowed spatial window  
        for (int i \= 0; i \< len1; i++) {  
            int start \= Math.max(0, i \- maxMatchDistance);  
            int end \= Math.min(i \+ maxMatchDistance \+ 1, len2);  
            for (int j \= start; j \< end; j++) {  
                if (\!s2Matches\[j\] && s1.charAt(i) \== s2.charAt(j)) {  
                    s1Matches\[i\] \= true;  
                    s2Matches\[j\] \= true;  
                    matches++;  
                    break;  
                }  
            }  
        }

        if (matches \== 0) return 0.0;

        // Phase 2: Calculate transpositions for out-of-sequence matches  
        int transpositions \= 0;  
        int k \= 0;  
        for (int i \= 0; i \< len1; i++) {  
            if (s1Matches\[i\]) {  
                while (\!s2Matches\[k\]) k++;  
                if (s1.charAt(i) \!= s2.charAt(k)) transpositions++;  
                k++;  
            }  
        }

        // Phase 3: Compute base Jaro score  
        double jaro \= ((matches / (double) len1) \+   
                       (matches / (double) len2) \+   
                       ((matches \- (transpositions / 2.0)) / matches)) / 3.0;

        // Phase 4: Apply Winkler Modification (Prefix scaling)  
        int prefixMatch \= 0;  
        for (int i \= 0; i \< Math.min(4, Math.min(len1, len2)); i++) {  
            if (s1.charAt(i) \== s2.charAt(i)) {  
                prefixMatch++;  
            } else {  
                break;  
            }  
        }

        // Apply standard scaling factor of 0.1  
        return jaro \+ (prefixMatch \* 0.1 \* (1.0 \- jaro));  
    }  
}

### **4\. API Client and Resilience Framework**

External API gateways are inherently volatile. Relying on synchronous HTTP calls without safeguards will result in thread exhaustion during downstream outages. The architecture utilizes Spring Boot 3's modern RestClient combined with the Resilience4j library. The KycProviderClient wraps network calls in a Circuit Breaker and a Retry mechanism. If the Karza or Cashfree APIs time out, the retry mechanism attempts the call again; if failure rates exceed a defined threshold, the circuit breaker opens, failing fast to protect the internal thread pools21.

Java  
package com.fooddelivery.onboarding.client;

import com.fooddelivery.onboarding.dto.GstVerificationResponse;  
import com.fooddelivery.onboarding.dto.PennyDropResponse;  
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;  
import io.github.resilience4j.retry.annotation.Retry;  
import org.springframework.beans.factory.annotation.Value;  
import org.springframework.stereotype.Component;  
import org.springframework.web.client.RestClient;  
import org.springframework.http.MediaType;  
import java.util.Map;

@Component  
public class KycProviderClient {

    private final RestClient restClient;

    @Value("${karza.api.key}")  
    private String karzaApiKey;

    @Value("${karza.base.url}")  
    private String karzaBaseUrl;

    public KycProviderClient(RestClient.Builder restClientBuilder) {  
        this.restClient \= restClientBuilder.build();  
    }

    /\*\*  
     \* Executes GSTIN validation using the Karza V2 endpoint.  
     \* Wrapped in a Resilience4j Retry mechanism to handle transient network faults.  
     \*/  
    @Retry(name \= "kycApiRetry")  
    @CircuitBreaker(name \= "kycApiCircuitBreaker")  
    public GstVerificationResponse verifyGstin(String gstin) {  
        return restClient.post()  
                .uri(karzaBaseUrl \+ "/v2/gst-verification")  
                .header("x-karza-key", karzaApiKey)  
                .contentType(MediaType.APPLICATION\_JSON)  
                .body(Map.of("consent", "Y", "gstin", gstin))  
                .retrieve()  
                .body(GstVerificationResponse.class);  
    }  
}

#### **Application Configuration (application.yml)**

The Resilience4j module must be explicitly configured to define the retry delays, exception targets, and the sliding window metrics for the circuit breaker evaluation21.

YAML  
resilience4j:  
  retry:  
    instances:  
      kycApiRetry:  
        maxAttempts: 3  
        waitDuration: 2s  
        retryExceptions:  
          \- java.io.IOException  
          \- org.springframework.web.client.HttpServerErrorException  
          \- org.springframework.web.client.ResourceAccessException  
  circuitbreaker:  
    instances:  
      kycApiCircuitBreaker:  
        slidingWindowSize: 10  
        failureRateThreshold: 50  
        waitDurationInOpenState: 30s  
        permittedNumberOfCallsInHalfOpenState: 3

### **5\. The Verification Orchestrator Service**

The Service layer orchestrates the core business logic. It executes within a transactional boundary, binding the physical database entities, the external REST calls, and the algorithmic identity matching together.

Java  
package com.fooddelivery.onboarding.service;

import com.fooddelivery.onboarding.domain.Brand;  
import com.fooddelivery.onboarding.domain.VerificationStatus;  
import com.fooddelivery.onboarding.repository.BrandRepository;  
import com.fooddelivery.onboarding.client.KycProviderClient;  
import com.fooddelivery.onboarding.dto.GstVerificationResponse;  
import com.fooddelivery.onboarding.util.JaroWinklerMatcher;  
import lombok.RequiredArgsConstructor;  
import lombok.extern.slf4j.Slf4j;  
import org.springframework.stereotype.Service;  
import org.springframework.transaction.annotation.Transactional;

@Slf4j  
@Service  
@RequiredArgsConstructor  
public class BrandVerificationService {

    private final BrandRepository brandRepository;  
    private final KycProviderClient kycProviderClient;  
      
    // Configured threshold for algorithmic identity matching  
    private static final double SIMILARITY\_THRESHOLD \= 0.85;

    @Transactional  
    public void executeBrandKyc(Long brandId) {  
        Brand brand \= brandRepository.findById(brandId)  
                .orElseThrow(() \-\> new IllegalArgumentException("Brand not found in database"));

        // Step 1: Synchronous GSTIN verification via external API  
        log.info("Initiating GSTIN check via Karza for Brand: {}", brandId);  
        GstVerificationResponse gstResponse \= kycProviderClient.verifyGstin(brand.getGstin());  
          
        if (gstResponse \!= null && "ACTIVE".equalsIgnoreCase(gstResponse.getStatus())) {  
            // Persist the exact legal name returned by the government network  
            brand.setLegalEntityName(gstResponse.getLegalName());  
            brand.setKycStatus(VerificationStatus.VERIFIED);  
            log.info("GSTIN verified successfully. Legal Entity: {}", brand.getLegalEntityName());  
        } else {  
            brand.setKycStatus(VerificationStatus.REJECTED);  
            brandRepository.save(brand);  
            throw new IllegalStateException("GSTIN Verification Failed. Entity is suspended or invalid.");  
        }

        brandRepository.save(brand);  
    }  
      
    /\*\*  
     \* Processes asynchronous webhook payloads received from the Penny Drop gateway.  
     \*/  
    @Transactional  
    public void processPennyDropWebhook(Long brandId, String registeredBankName, boolean isSuccessful) {  
        Brand brand \= brandRepository.findById(brandId)  
                .orElseThrow(() \-\> new IllegalArgumentException("Brand not found"));  
                  
        if (isSuccessful) {  
            brand.setBankBeneficiaryName(registeredBankName);  
              
            // Step 2: Algorithmic Identity Resolution via Jaro-Winkler  
            double matchScore \= JaroWinklerMatcher.computeSimilarity(  
                    brand.getLegalEntityName(),   
                    registeredBankName  
            );  
              
            log.info("Jaro-Winkler Score between '{}' and '{}' is: {}",   
                     brand.getLegalEntityName(), registeredBankName, matchScore);

            if (matchScore \>= SIMILARITY\_THRESHOLD) {  
                brand.setPennyDropStatus(VerificationStatus.VERIFIED);  
                log.info("Identity verification passed. Brand ready for outlet onboarding.");  
            } else {  
                log.warn("Identity Mismatch (Score: {}). Manual compliance review required for Brand: {}",   
                         matchScore, brandId);  
                brand.setPennyDropStatus(VerificationStatus.REJECTED);  
            }  
        } else {  
            brand.setPennyDropStatus(VerificationStatus.FAILED);  
        }  
          
        brandRepository.save(brand);  
    }  
}

## **Production Reliability, Auditing, and Edge Cases**

Operating at the scale of a national food delivery provider entails resolving severe edge cases and enforcing strict architectural patterns to maintain data consistency across distributed microservices.  
A primary challenge lies in the dual-write problem. Upon successful verification of an outlet's FSSAI license and its geospatial PostGIS coordinates, the downstream Search cluster (e.g., Elasticsearch) and the Logistics cluster (the Kafka-driven Dispatch Engine) must be immediately notified to make the restaurant "Live" and discoverable to customers. If the backend commits the activation status to the PostgreSQL database but subsequently fails to publish the message to the Apache Kafka broker due to a network partition, the system enters an inconsistent state where the restaurant believes it is active, but customers cannot see it.  
To prevent this, the architecture mandates the implementation of the Transactional Outbox Pattern. The orchestrating service does not send the message directly to Kafka. Instead, it serializes a RestaurantActivatedEvent and inserts it into an outbox\_events table within the exact same PostgreSQL transaction that updates the is\_active flag on the outlets table. Because this occurs within a single ACID-compliant database transaction, it is guaranteed to either entirely succeed or entirely roll back. A background process, typically a Debezium Change Data Capture (CDC) connector or a scheduled polling agent, reads this outbox table and guarantees at-least-once delivery to the Kafka topics, ensuring absolute eventual consistency across the platform.  
Furthermore, because food delivery platforms are subject to extreme regulatory scrutiny by municipal health authorities and federal tax bodies, full audit traceability is non-negotiable. Every API request transmitted to, and every JSON response received from, the identity providers (such as the GSTN network or FSSAI registries) must be archived immutably. The verification\_audit\_logs table serves this purpose. However, persisting heavy JSON blobs synchronously adds massive latency to the merchant onboarding flow. The system architecture dictates that the persistence of these audit logs must be executed asynchronously using Spring's @Async event publishers. This offloads the database insert operations to a separate thread pool, keeping the primary HTTP threads highly responsive and ensuring a seamless, zero-latency user experience for the merchant completing their registration.

#### **Works cited**

> 1. Swiggy Restaurant Partner Registration: 2026 Complete Guide, [https://www.agileregulatory.com/blogs/swiggy-restaurant-partner-registration-process-in-2026](https://www.agileregulatory.com/blogs/swiggy-restaurant-partner-registration-process-in-2026)  
> 2. Swiggy Restaurant Partner Registration: A Complete Guide \- Foodiv, [https://www.foodiv.com/swiggy-restaurant-partner/](https://www.foodiv.com/swiggy-restaurant-partner/)  
> 3. 6 Steps to Register Your Restaurant on Swiggy \- UrbanPiper, [https://www.urbanpiper.com/blog/how-to-register-your-restaurant-on-swiggy](https://www.urbanpiper.com/blog/how-to-register-your-restaurant-on-swiggy)  
> 4. Requirements for Registering your Food Business on Zomato and Swiggy, [https://www.kouzinafoodtech.com/blog/can-i-list-on-zomato-without-gst-registration](https://www.kouzinafoodtech.com/blog/can-i-list-on-zomato-without-gst-registration)  
> 5. Partner with Swiggy: A Complete Guide, [https://blog.swiggy.com/food/complete-guide-to-partner-with-swiggy/](https://blog.swiggy.com/food/complete-guide-to-partner-with-swiggy/)  
> 6. Swiggy Partner Registration Process for Restaurants \- IndiaFilings, [https://www.indiafilings.com/learn/swiggy-registration](https://www.indiafilings.com/learn/swiggy-registration)  
> 7. Food Delivery App, uploaded:Food Delivery App  
> 8. PostGIS Fundamentals \- Atlantbh, [https://atlantbh.com/blog/postgis-fundamentals/](https://atlantbh.com/blog/postgis-fundamentals/)  
> 9. Creating spatial tables with PostGIS \- sql \- GIS Stack Exchange, [https://gis.stackexchange.com/questions/8699/creating-spatial-tables-with-postgis](https://gis.stackexchange.com/questions/8699/creating-spatial-tables-with-postgis)  
> 10. foss4g-2018-workshop-postgresql-postgis-for-beginners-aemde.md \- GitHub, [https://github.com/astroidex/foss4g-2018-workshop-postgresql-postgis-for-beginners-aemde/blob/master/foss4g-2018-workshop-postgresql-postgis-for-beginners-aemde.md](https://github.com/astroidex/foss4g-2018-workshop-postgresql-postgis-for-beginners-aemde/blob/master/foss4g-2018-workshop-postgresql-postgis-for-beginners-aemde.md)  
> 11. 9\. Geometries — Introduction to PostGIS, [http://postgis.net/workshops/postgis-intro/geometries.html](http://postgis.net/workshops/postgis-intro/geometries.html)  
> 12. Jaro-Winkler vs. Levenshtein in AML Screening: Choosing the Right Algorithm \- Flagright, [https://www.flagright.com/post/jaro-winkler-vs-levenshtein-choosing-the-right-algorithm-for-aml-screening](https://www.flagright.com/post/jaro-winkler-vs-levenshtein-choosing-the-right-algorithm-for-aml-screening)  
> 13. Java example of Jaro Winkler \- It's Smee Blog, [https://itssmee.wordpress.com/2010/06/28/java-example-of-jaro-winkler/](https://itssmee.wordpress.com/2010/06/28/java-example-of-jaro-winkler/)  
> 14. Jaro and Jaro-Winkler similarity \- GeeksforGeeks, [https://www.geeksforgeeks.org/dsa/jaro-and-jaro-winkler-similarity/](https://www.geeksforgeeks.org/dsa/jaro-and-jaro-winkler-similarity/)  
> 15. Jaro similarity \- Rosetta Code, [https://rosettacode.org/wiki/Jaro\_similarity](https://rosettacode.org/wiki/Jaro_similarity)  
> 16. GST GSTIN Verification | TKYC APIs \- Postman, [https://www.postman.com/material-geologist-93175419/api-testing/request/83pxkgc/gst-gstin-verification](https://www.postman.com/material-geologist-93175419/api-testing/request/83pxkgc/gst-gstin-verification)  
> 17. Legal Entity Identifier (LEI) | TKYC APIs \- Postman, [https://www.postman.com/material-geologist-93175419/workspace/api-testing/request/19653723-27a2ffa1-f2bc-48ba-a47e-fdbdd6cfb4a9](https://www.postman.com/material-geologist-93175419/workspace/api-testing/request/19653723-27a2ffa1-f2bc-48ba-a47e-fdbdd6cfb4a9)  
> 18. Best Bank Account Verification API India | Real-Time Account Validation & Penny Drop Verification \- Noble Web Studio, [https://www.noblewebstudio.com/blog/bank-account-verification-api/](https://www.noblewebstudio.com/blog/bank-account-verification-api/)  
> 19. Reverse Drop Webhooks \- Cashfree Payments, [https://www.cashfree.com/docs/api-reference/vrs/v2/reverse-penny-drop/webhooks-reverse-penny](https://www.cashfree.com/docs/api-reference/vrs/v2/reverse-penny-drop/webhooks-reverse-penny)  
> 20. Cashfree Payments (llmstxt/cashfree\_llms\_txt) | Context7, [https://context7.com/llmstxt/cashfree\_llms\_txt](https://context7.com/llmstxt/cashfree_llms_txt)  
> 21. Guide to Resilience4j With Spring Boot \- Baeldung, [https://www.baeldung.com/spring-boot-resilience4j](https://www.baeldung.com/spring-boot-resilience4j)  
> 22. Resilience4J Retry not auto-configured in Spring boot 3 \- Stack Overflow, [https://stackoverflow.com/questions/74874199/resilience4j-retry-not-auto-configured-in-spring-boot-3](https://stackoverflow.com/questions/74874199/resilience4j-retry-not-auto-configured-in-spring-boot-3)

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAbCAYAAAB836/YAAAA80lEQVR4XmNgGAW0AvZA/B8Jb4aKg9gkA5Dm52hiKQwQw3aiiRME+gy4XQESt0IXJAS+AfEfdEEowGURXgALM2xgHboAMeALA8LQHUBsgCpNOuBkQI1dGFZDVkQO0AXi7QyohlINvGDAbSAoiHLQBWEgGF0AChYz4DYwFYg50AVBwA+IC9AFoaCUAbeBOMFZBtzJ4i8Qz0AT0wDi9UD8CU0cDmABz4MmvhaIP6OJgUA3lMbp8idAzATEHxggit5D6QVIatABIwMeA8kBeQyQpEU18BaIzdEFKQFU9S4zA5UMBEVaMxB/BWIBNDmyAVXDbRgAALhyPNLdoeQaAAAAAElFTkSuQmCC>

[image2]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABIAAAAaCAYAAAC6nQw6AAAA0UlEQVR4Xu2QMQ5BQRRFR0QlNoBSRWMBEo1CJdEpxRJENCIilqCSKGxFJDag0uppNRLu/f/98TxTKhRzkhOc9zJ/Pucikd/RhBtYlN8lOIdjmMuWwBTuYEU1TwEeYA8+4cqly2QhrQ7PMA/L0qqy49nL59ClC8v3KLkZ20U1wjYxLXkFwidyQTMKNN6KrWG6h8OjabyJPYg3tu0DDruBlr26blfTPAMXfgpbO9A68v2uB+Tkvg8K/T8t1WawpmYJD7g2bQtvphE2Hta3g0jkb3gB7yMuJV7Xm78AAAAASUVORK5CYII=>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAcAAAAcCAYAAACtQ6WLAAAAe0lEQVR4XmNgGOTAEYht0QVh4D8Qr0UXhAGQZAG6IAjoM0AkmZAFbYDYC4h3QyV9oXwwKALiEqjEWygfhFEASDIXXRAEdBkgkozoEiCwhgEiiRW8ZsAjCZLYhMTfhsQGS1pA2RlIbDAASYK8UwfEK5AlYADkeQl0wREPAGL/GMEfWDMiAAAAAElFTkSuQmCC>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB0AAAAaCAYAAABLlle3AAAA1ElEQVR4XmNgGIlADV2ASkAUXQAZ/EcXoBI4hC6ADEYtpRQMLUu9gHgJEEegSxAByLL0MxCnQNnSQPwUSY4YQLKldgyo4n+B+CcSnxhAsqUlDBDxE0BsiyYHA1IMEMfgAiRbCgIgcWSMDLqAOBWLODIgy1IQ4AbiZQwQNTloco5QcVyAJEt/YxED8YXRxKhqKYgfhcQPAuJfSHwYoKql7AyQ7AKLy5Wo0nBAVUuJBSPD0u9A/A6I3wDxByB2QZUGA6pbSgzAa6kJugCVgDy6wCgYfgAArJ5HXfrdOboAAAAASUVORK5CYII=>

[image5]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB0AAAAaCAYAAABLlle3AAABA0lEQVR4Xu2TvQ4BURCFp1OoRFQKnUKiU2iIWkkjCiFRikJ0XsFjiETjIVQKr6FUKcRfmMndYe74yV5u4+dLTnLPObs5m90swC+S1oEnEjqQnHTgiZkOJP/Rd/ms0TJqhKrpIgQvja5R7eCcRC1FFwbn0SLY+RG1FT4MzqN9MPkcVVAdkQfzIAdUya4uOI8SlEsxMVRVeOqywjMvjRJR1BjMNZ0gawSe2aAWwjNOo/s7Gfm4yhjqhjoEx1HydeErqJ3wkgzc3s84jUbA/C78LSd2bfHoYQin0bCsxHkqzoz3UXoTTVQL1UUNrNbgdbQH11fP8vrLvMPT0ZwOPJHSwZ/v4wx7/0x118eswQAAAABJRU5ErkJggg==>

[image6]: <data:image/png;base64,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>

[image7]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAI8AAAAcCAYAAABYkex+AAAD50lEQVR4Xu2aW8gNURTHl7uQ3MolHqTcrymXyJM8IJd4kORDPCDeeOHFixciyT2XB8UDKZ6U9EWh3Aq5X3O/3ymE9bf3/s4668xMM+cbM9+cM7/6d/Zae2afmX3+s/eemUPUsJnDam3LfWRFGdzSCcFznYhIV/s5mDVUVuSkQxfWUhH3FeVyuK0Tghc6EZFuonyQ1VzEOSnwU8X9VByVIPO81ImISPOAZyrOSRBcuVdUrr+KoxJknlc6ERFtnt8qzrFsZ/0hM6VcY11lHWHNYB0gM2I0rdvaxEtYb1ljbW4rmTagHqL8wNZPZtXYsmOAisF51n7WR13hQZB5Xqu4Les76wRrtqrzQptnF2uUylU8v3TChz1UfHXhh59uy7NY50TdV1HGdhLELckYULKBNUzlBqp4MWuBLet2vQgyzxsVu/Z6s27ICh+0eZazlqmcH2GOPROEPZGdZEYgh9xvKuueiBuz1rOuU2n7nWyug8pjROmocoNUvJDMvt/IfAfAaHWHNddtJAgyD0ZFCS4MtF0rcsdZI21eo80zjbVR5fzwai+ThD2RbazNIpb7Ycq5b8ujqXSEkrRiXWSdVflDrCEqp83Tgswd2WEqbncE1d88YDyZKdfd8bnvOMkabssObR6MOjNVzg/dJ0mDJcYPnSyHsCeym4yBHHK/KVRYu1xi7bXlJmS2c89EwBP7+Yk1QeRXUelaQ5pnLRUvck+Jchjz1LJWi1iaB8fontXsY7UrVP3Da2rX5kHftFc5P8L2edzcJPPdTvUmTCM7WO+strC+kFkzYNGKKes9mR/DTV0wyBnWJjI/Bn7EY2T2wb4A6yIZ42q4YMsOaZ5GZPY5TaYtCcwzT+WANA/Mj+nOIc0D0z5i3WVNEnmwhkrNBLR5vAzmR5g+/5/gwo3lGGJpJCb0sehpyw+YZ75OUum05aZW4DVtaSaymtlyd1lBpeZ5qOIg9HkmTUWaB2sevJ5whDEPRo3HZDoEU59EmgdtdxZxGPMEDfHSPLiRwB1kWLzaS5KKNA9YSYWrPYx5gtAjjySMeYJw5unJGiMrQpB2n1eseaqBtPs8MfPIoTtXNPkRVCfR7QVpnd0nDImZJyd+0u7z3DwZJu0+r2rz1JA5bjy36aXqskDafV615hlHhWcueF2B43d3Z1kh7T7HRRfLMcTSSILg7b48ZpTxEjZLpNXnn8n8/RbPxaCnZN4alD16p3UicYHjx4vZLJH1Pq8jyyeyiPVBJzNAlvu8iKyeSBsyw24WyWqfl4ATkcLfMLPAZVHGK42GDNYVup9zUgJ/BZlH5i+pKyj6u6WcKuUo5VdxTk5OThXxF28sUbJ8MW4TAAAAAElFTkSuQmCC>

[image8]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAaCAYAAACtv5zzAAABBklEQVR4Xu2UPQ/BUBSGD2ITg91qMfgBxGRl8T/EwCKEySImC7E0zEwWm8lmMRjEaDOQmH28N71NT2+R6L1jn+RJT9+enFOaXKIQDTJwAsssa7A6MHH4hFOYhEX4gm14Z32BEcMKakh23lLDf7HIHvQJkYtfp4UY8muBNs6CofrAFANylziOPR0GqJF/ydHTYZAS/f4uf1FVA8mcDCyowLoaSppkYMEOLtVQ8iDvh+7CDczJ+yzZR4rg64s4/3NCyRfkPx7ycAR78n5P7nl1klcfZxiFN7IXXeXVYj0c8SzCaoc+q7XgQ3l9YXVgYuQOTbFanLxpWWuzhgc4gyu4hR1PR0iIMd4LmD/v2Yy3vgAAAABJRU5ErkJggg==>

[image9]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAaCAYAAAC+aNwHAAAApElEQVR4XmNgGAXoYAIQfwTi/1D8HYjfoYmtgqvGA2CK0YE8A0R8F7oEOgApOo4uCAW4DIeDCAaIAnd0CSDgZCDCgGsMuBWsZ4DIBaBLIANcNjgyQMQnokugA5gBH4D4PRD/gPIvA7EwkjqsAOb/JHQJYsFNBuzOJxrg8j/RAKT5DrogsaCaAWJAOroEITAZiD8zQEIclO6/AvE/FBWjYBTQGgAA/GAxOhsgBN4AAAAASUVORK5CYII=>

[image10]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAaCAYAAACHD21cAAAAk0lEQVR4XmNgGHlgHhB/AuL/SPgjEPchK8IHYJpIAowMEE1n0SUIgWwGiEYvdAlC4CUDGc4EAbL8BwIgTSfQBQkBQv5zQheAgdcM+J0JilOsAJ//aoDYEV0QBJgZIJouoksAgSwDbgMZ+hkgkoFo4jOg4hfQxBkWA/EvIP4LxP8YEM4FYRD/DxB/B2IZmIZRMAoYALcBKZyfMMC3AAAAAElFTkSuQmCC>

[image11]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAmwAAAA4CAYAAABAFaTtAAAEWklEQVR4Xu3dSYskRRQA4HBDEMUFL+LCIKKiIK54dPSgoLjgQX+AeFA8COI2J0EEcUEUD4qiA568eBE3EFQEL+KC3lQYcAEviuK+G4+soqPfZFZXFtVd3cz3waMnXvRkZFUOxCMyM6YUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABYgeNyYhu5PCcAgH67avw3idNrXLqud2s9VtbO5eoa+9Z3b4qza3xQ4+MaH9V4v8Y7NS5rf2mJ9pbu+K/WeLvGrW3nksX3uAqflrXreNTk55AvcwIAWO+KGnc37fvK7Ml1M31f46CmvbvGL017M51W9v/cUbjFOW2G12v8m5NL9lxq31XWiqjNlI//Vo09KdeKVbatus4AsCPlyTUKppzbKn3jzprohxxW486c3MAzNf5KudtL/zktQxz30ZxcsqFzH8ovSz7+xaW7JrPkvwMATBxduony4JS/PrW3yrIm7SPK+IItxo7VxtY/Nf5Iuan3alyUk9WbOTFgzGeN27RZrAgekpPJ7zkxMWbssa4t+x//pNTu80SNI3MSAOjE5DqNl1PfVmvP5cbUN0Y8M7VIwdba1ZPLohDZ3bTjubR5bXTs7LPmz2fUOLNp97myxs05OTF27LHa6/hU6pvljZwAANZE4fFt6SbY11LfVruurE32P6e+eS1asD1f49kaT5b1z9LNcnLpXtL4O3fMEC8ZLFI0fV66Yu2s3NHj8Rrn5+TEImOPdUdZu47tM5KzLHq9AeCAclPZmsk8iyIkbmO2ppN968HUbl3TRKzO7U25WZ4u+481Rjz7NmZ7ihjrkZycQ7wA8UNODojic1dOTmz0WS8o67+7HGO8UOOLlFvVyh8A7DjH5ER1YVn8ttRDc8SQD3Oi+qnMX5xkY1fYpqtri5ie+ydl/m1AYrxjU26jZ99itWx6GzQXQH3uKcPbs2xWYdRXzMV1uDcnB0RBCgA0Yt+xq1Kuncj3Ne38c9niuA/35FpR4My7DcYiBdsi4jtsHV/jkpTL4vZgHu+30t2SHnJu6fbHa7XPtPWJ26YP5OREHn9Z4iWNE5v2oWX9WCdMfvaNH31D5wsAB6w/S7ciFJNnPDvUN4lOczERhzG3/ca4v8appRsvVtWGNlJ9JScGzFuwxef+scZ3pXujcqOtJ1qx5UefGHtIFGbxXcbnnEa0f21/qcc8b1n2ydf0ttIVhl/V+KZ057NMX9e4pXTjxr+vvet6O0PbxsRbtwDAAuIFgBDPtoV46H1Vzindis08Dq9xQ04egPoKo1V7qXS3a7PteK4AsCPEKknsARZ7kc3z3NRmilUwxnsxJ1asrzCLVcZ5i3EAYBvrm+jZ2Ls5sWL5OsamzeelHACww5xSujdG2dniv+Fa9jNzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADb1v+uOdSTf5VgZAAAAABJRU5ErkJggg==>