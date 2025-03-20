package com.shaderbox.ai.comp.shaders

import android.graphics.RuntimeShader
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shaderbox.ai.data.IShaderScreen

class DreamscapeChromaticAberrationShader(
    override val name: String = "Chromatic Aberration",
    override val speed: Float = 0.01f,
    override val shader: RuntimeShader = RuntimeShader(
        """
            uniform shader composable;
            uniform float time;
            uniform float2 pointer;
            uniform float2 resolution;
            
            float hash(float2 p) {
                float h = dot(p, float2(127.1, 311.7));
                return fract(sin(h) * 43758.5453123);
            }
            
            float noise(float2 p) {
                float2 i = floor(p);
                float2 f = fract(p);
                f = f * f * (3.0 - 2.0 * f);
                float a = hash(i);
                float b = hash(i + float2(1.0, 0.0));
                float c = hash(i + float2(0.0, 1.0));
                float d = hash(i + float2(1.0, 1.0));
                return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
            }
            
            float fbm(float2 p) {
                float value = 0.0;
                float amp = 0.5;
                float freq = 2.0;
                for(int i = 0; i < 5; i++) {
                    value += amp * noise(p * freq);
                    amp *= 0.5;
                    freq *= 2.0;
                }
                return value;
            }
            
            half4 main(float2 fragCoord) {
                // Convert coordinates to UV space (0.0 to 1.0)
                float2 uv = fragCoord / resolution;
                
                float2 aspect = float2(resolution.x/resolution.y, 1.0);
                uv = uv * aspect;
                float2 pointerPos = pointer * aspect;
                
                float dist = length(uv - pointerPos);
                float pointerInfluence = smoothstep(0.5, 0.0, dist);
                
                float2 offset = float2(
                    fbm(uv + time * 0.1),
                    fbm(uv + time * 0.1 + 5.0)
                );
                
                float r = fbm(uv + offset + float2(pointerInfluence * 0.1, 0.0));
                float g = fbm(uv + offset);
                float b = fbm(uv + offset - float2(pointerInfluence * 0.1, 0.0));
                
                half3 color = half3(
                    r * (0.8 + 0.2 * sin(time)),
                    g * (0.8 + 0.2 * sin(time + 2.0)),
                    b * (0.8 + 0.2 * sin(time + 4.0))
                );
                
                color = mix(color, half3(1.0), pointerInfluence * 0.3);
                
                return half4(color, 1.0);
            }
        """
    )
) : IShaderScreen

@Preview(showBackground = true)
@Composable
private fun DreamscapeChromaticAberrationShaderPreview() {
    val shader = DreamscapeChromaticAberrationShader()
    CardShader(shader.shader)
}