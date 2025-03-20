package com.shaderbox.ai.comp.shaders

import android.graphics.RuntimeShader
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shaderbox.ai.data.IShaderScreen

class DreamscapeShader(
    override val name: String = "Dreamscape",
    override val speed: Float = 0.01f,
    override val shader: RuntimeShader = RuntimeShader(
        """
            uniform shader composable;
            uniform float2 resolution;
            uniform float time;
            uniform float2 pointer;
            
            float hash(float2 p) {
                return fract(sin(dot(p, float2(127.1, 311.7))) * 43758.5453);
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
                float amplitude = 0.5;
                float frequency = 2.0;
            
                for(int i = 0; i < 6; i++) {
                    value += amplitude * noise(p * frequency);
                    amplitude *= 0.5;
                    frequency *= 2.0;
                }
            
                return value;
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / resolution;

                float2 aspect = float2(resolution.x/resolution.y, 1.0);
                uv = uv * 2.0 - 1.0;
                uv *= aspect;
            
                float pointerInfluence = length(uv - (pointer * 2.0 - 1.0) * aspect);
                pointerInfluence = 1.0 - smoothstep(0.0, 0.5, pointerInfluence);
            
                float2 motion = float2(time * 0.1);
                float f1 = fbm(uv * 3.0 + motion);
                float f2 = fbm(uv * 2.0 - motion + f1);
                float f3 = fbm(uv * 4.0 + f2 + motion * 0.5);
            
                half3 color1 = half3(0.5, 0.8, 1.0);
                half3 color2 = half3(0.8, 0.3, 0.8);
                half3 color3 = half3(0.1, 0.1, 0.4);
            
                f3 += pointerInfluence * 0.5;
            
                half3 finalColor = mix(color1, color2, f1);
                finalColor = mix(finalColor, color3, f2);
                finalColor += f3 * 0.3;
            
                return half4(finalColor, 1.0);
            }
        """
    )
) : IShaderScreen

@Preview
@Composable
private fun DreamscapeShaderEffectPreview() {
    val shader = DreamscapeShader()
    CardShader(shader.shader)
}