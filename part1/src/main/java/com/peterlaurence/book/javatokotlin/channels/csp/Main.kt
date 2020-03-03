package com.peterlaurence.book.javatokotlin.channels.csp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun main() = runBlocking<Unit> {
    val shapeChannel = Channel<Shape>()
    val shapeLocationChannel = Channel<Location>()

    with(ShapeCollector(16)) {
        start(shapeLocationChannel, shapeChannel)
        consumeShapes(shapeChannel)
    }

    collectNewShapes(shapeLocationChannel)
}

fun CoroutineScope.consumeShapes(shapesInput: ReceiveChannel<Shape>) = launch {
    for (shape in shapesInput) {
        // do something useful with shapes
        println("Rendering $shape")
    }
}

fun CoroutineScope.collectNewShapes(locationsOutput: SendChannel<Location>) = launch {
    while (true) {
        /* Simulate fetching some shape location */
        val location = Location(Random.nextInt(), Random.nextInt())
        locationsOutput.send(location)
    }
}


