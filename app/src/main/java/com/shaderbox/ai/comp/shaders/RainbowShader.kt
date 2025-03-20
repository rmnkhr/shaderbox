package com.shaderbox.ai.comp.shaders

import android.graphics.RuntimeShader
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shaderbox.ai.data.IShaderScreen

class RainbowShader(
    override val name: String = "Rainbow",
    override val speed: Float = 0.01f,
    override val shader: RuntimeShader =
        RuntimeShader(
            """
            uniform shader composable;
            uniform float2 resolution;
            uniform float2 pointer;
            uniform float time;

            half3 rainbowGradient(float t) {
                return 0.5 + 0.5 * cos(6.28318 * (half3(0.0, 0.333, 0.666) + t));
            }

            float flowPattern(float2 uv, float time) {
                return sin(uv.x * 10.0 + time) * 0.5 + 0.5;
            }

            float2 gentleDisplacement(float2 uv, float2 pointer) {
                float dist = length(uv - pointer);
                return uv + 0.05 * sin(uv.xy * 10.0 + dist * 10.0 + time);
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / resolution;
                uv.x *= resolution.x / resolution.y;
                uv = gentleDisplacement(uv, pointer);

                float pattern = flowPattern(uv, time);
                half3 color = rainbowGradient(pattern);

                return half4(color, 1.0);
            }
        """
        )
) : IShaderScreen

@Preview(showBackground = true)
@Composable
private fun RainbowShaderEffectPreview() {
    val rainbowShader = RainbowShader()
    CardShader(rainbowShader.shader)
}
