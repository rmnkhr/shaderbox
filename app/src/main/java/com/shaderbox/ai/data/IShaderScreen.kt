package com.shaderbox.ai.data

import android.graphics.RuntimeShader

interface IShaderScreen {
    val name: String
    val speed: Float
    val shader: RuntimeShader
}