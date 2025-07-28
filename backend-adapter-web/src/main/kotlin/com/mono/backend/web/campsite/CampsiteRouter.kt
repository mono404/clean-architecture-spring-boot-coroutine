package com.mono.backend.web.campsite

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

@Component
class CampsiteRouter(
    private val campsiteHandler: CampsiteHandler
) {
    @Bean
    fun campsiteRoutes(): CoRouterFunctionDsl.() -> Unit = {
        "/import".nest {
            POST("/20241130", campsiteHandler::importCsvFile1)
            POST("/csv2", campsiteHandler::importCsvFile2)
        }
        "/search".nest {
            GET("", campsiteHandler::searchCampsitesByName)
            GET("/radius", campsiteHandler::getCampsitesWithinRadius)
            GET("/nearest", campsiteHandler::getNearestCampsites)
            GET("/bounds", campsiteHandler::getCampsitesWithinBounds)
        }
        "/location".nest {
            GET("/province/{province}", campsiteHandler::getCampsitesByProvince)
            GET("/province/{province}/city/{city}", campsiteHandler::getCampsitesByCity)
        }
        "/statistics".nest {
            GET("", campsiteHandler::getStatistics)
        }
        "/admin".nest {
            DELETE("/clear", campsiteHandler::clearAllData)
        }
        "/{campsiteId}".nest {
            GET("", campsiteHandler::getCampsite)
        }
        GET("", campsiteHandler::getCampsites)
    }
} 