package com.example.gg_dyplom

class PolygonModelClass {
    var features: List<FeaturesPolygon>? = null

    override fun toString(): String {
        return "All $features"
    }
}

class FeaturesPolygon {
    var geometry: GeometryPolygon? =null
    var attributes: Attributes? = null

    override fun toString(): String {
        return "Markers (geometry=$geometry, attributes=$attributes)"
    }
}

class GeometryPolygon {
    var rings: List<List<List<Double>>>? = null

    override fun toString(): String {
        return "Geometry (rings=[$rings])"
    }
}

class  Attributes {
    var FID: Int? = null
    var Id: Int? = null
    var zone: String? = null
    var number: String? = null

    override fun toString(): String {
        return "Properties (Id=$FID, pietro=$Id, strefa=$zone, numery komunikatów=$number)"
    }
}