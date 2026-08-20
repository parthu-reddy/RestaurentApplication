package contracts
import org.springframework.cloud.contract.spec.Contract
/*
 * Corrected 2026-08-20. This asserted a list of objects {id, name, address, status}.
 * InternalRestaurantController.getOwnerOutlets returns ResponseEntity<List<String>> -- a JSON array
 * of outlet id strings -- so the contract could never pass.
 *
 * The duplicate placeholder sample.groovy, which asserted [] for this same endpoint, was deleted:
 * two contracts on one route with incompatible bodies cannot both hold against one fixture.
 */
Contract.make {
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/internal/restaurants/owner/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/outlets')), producer('/api/v1/internal/restaurants/owner/123e4567-e89b-12d3-a456-426614174000/outlets')))
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
            "123e4567-e89b-12d3-a456-426614174000"
        ])
    }
}
