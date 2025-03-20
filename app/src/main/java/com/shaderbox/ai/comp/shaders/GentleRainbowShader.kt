package com.shaderbox.ai.comp.shaders

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shaderbox.ai.data.IShaderScreen

class GentleRainbowShader(
    override val name: String = "Gentle Rainbow Gradient",
    override val speed: Float = 0.01f,
    override val shader: RuntimeShader =
        RuntimeShader(
            """
            uniform shader composable;
            uniform float time;
            uniform float2 pointer;
            uniform float2 resolution;
            
            
            half3 rainbowGradient(float t) {
                return 0.5 + 0.5 * cos(6.28318 * (half3(0.0, 0.333, 0.666) + t));
            }
    
            float pointerDistance(float2 uv) {
                return length(uv - pointer);
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / resolution;
                uv.x *= resolution.x / resolution.y;
                float t = uv.x + 0.5 * sin(time * 0.5);
                half3 color = rainbowGradient(t);
                
                // React to the pointer position by displacing the gradient
                float dist = pointerDistance(uv);
                float displacement = 0.1 * sin(dist * 10.0 - time);
                color = rainbowGradient(t + displacement);
    
                return half4(color, 1.0);
            }
            """
        ),
) : IShaderScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
private fun GentleRainbowShaderPreview() {
    val shader = GentleRainbowShader()
    CardShader(shader.shader)
}