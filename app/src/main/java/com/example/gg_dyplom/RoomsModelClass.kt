package com.example.gg_dyplom

class RoomsModelClass {
    var features: List<Features>? = null

    override fun toString(): String {
        return "All $features"
    }

    class Features {
        var geometry: Geometry? =null
        var properties: Properties? = null

        override fun toString(): String {
            return "Rooms (geometry=$geometry, properties=$properties)"
        }
    }

    class Geometry {
        var coordinates: List<Double>? = null

        override fun toString(): String {
            return "Geometry (coordinates=[$coordinates])"
        }
    }

    class  Properties {
        var SHORTNAME: String? = null
        var FLOOR_ID: Int? = null

        override fun toString(): String {
            return "Properties (Room number=$SHORTNAME, floor=$FLOOR_ID)"
        }
    }
}