
import org.springframework.cloud.contract.spec.Contract

/*
 * Corrected 2026-08-20. This asserted id/name at the ROOT. getRestaurant returns
 * ResponseEntity<ApiResponse<Map<String,Object>>>, i.e. {success, message, data:{...}}, so the
 * contract omitted the envelope and could never pass. Its sibling getNearbyRestaurants.groovy
 * already models the envelope correctly.
 */
Contract.make {
    description("should return restaurant by id")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/restaurants/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}')), producer('/api/v1/restaurants/123e4567-e89b-12d3-a456-426614174000')))
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            success: true,
            data: [
                id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
                name: 'Test Restaurant'
            ]
        ])
    }
}
