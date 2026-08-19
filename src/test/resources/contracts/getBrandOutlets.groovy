
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("should return brand outlets")
    request {
        method 'GET'
        urlPath(value(consumer(regex('/api/v1/restaurants/brands/[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}/outlets')), producer('/api/v1/restaurants/brands/123e4567-e89b-12d3-a456-426614174000/outlets'))) {
            queryParameters {
                parameter 'lat': value(consumer(regex('-?\\d+(\\.\\d+)?')), producer('12.9716'))
                parameter 'lng': value(consumer(regex('-?\\d+(\\.\\d+)?')), producer('77.5946'))
                parameter 'radius': value(consumer(regex('\\d+(\\.\\d+)?')), producer('5.0'))
            }
        }
    }
    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([
            success: true,
            data: [
                [
                    id: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
                    name: 'Test Outlet'
                ]
            ]
        ])
    }
}
