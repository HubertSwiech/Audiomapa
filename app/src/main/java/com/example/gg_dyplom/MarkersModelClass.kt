package com.example.gg_dyplom


class MarkersModelClass{
    var features: List<Features>? = null

    override fun toString(): String {
        return "All $features"
    }
}

class Features {
    var geometry: Geometry? =null
    var properties: Properties? = null

    override fun toString(): String {
        return "Markers (geometry=$geometry, properties=$properties)"
    }
}

class Geometry {
    var coordinates: List<Double>? = null

    override fun toString(): String {
        return "Geometry (coordinates=[$coordinates])"
    }
}

class  Properties {
    var IDP: Int? = null
    var FLOOR: Int? = null

    override fun toString(): String {
        return "Properties (Id=$IDP, pietro=$FLOOR)"
    }
}
