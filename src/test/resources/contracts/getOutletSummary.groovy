package contracts
import org.springframework.cloud.contract.spec.Contract

// Consumers: CustomerApplication RestaurantClient (reads timeZone for the outlet's earnings periods)
// and LedgerService RestaurantSummaryClient (reads name and brandName).
Contract.make {
    description("an outlet's name, brand and IANA time zone")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/internal/restaurants/outlets/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/summary')), producer('/api/v1/internal/restaurants/outlets/123e4567-e89b-12d3-a456-426614174000/summary')))
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            id: '123e4567-e89b-12d3-a456-426614174000',
            name: 'Test Restaurant',
            brandName: $(consumer('Test Brand'), producer(regex('.+'))),
            timeZone: 'Asia/Kolkata'
        ])
    }
}
