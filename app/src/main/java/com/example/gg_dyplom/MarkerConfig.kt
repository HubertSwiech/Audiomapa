package com.example.gg_dyplom


data class MarkerConfig(
    var textSize: Float = 30f,
    var threeDigitsNumTextSizeOffset: Float = 2f,
    var threeDigitsNumTextPositionX: Float = 8f,
    var threeDigitsNumTextPositionY: Float = 40f,
    var twoDigitsNumTextPositionX: Float = 14f,
    var twoDigitsNumTextPositionY: Float = 40f,
    var oneDigitsNumTextPositionX: Float = 22f,
    var oneDigitsNumTextPositionY: Float = 40f,
    var warningTextPositionX: Float = 8f,
    var warningTextPositionY: Float = 57f,
    var warningTextSizeOffset: Float = 2f
) {

    enum class MarkerSizeFactor(val value: Int) {
        SMALL(1),
        BIG(2)
    }
}
