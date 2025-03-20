package com.shaderbox.ai.comp.shaders

import android.graphics.RuntimeShader
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shaderbox.ai.data.IShaderScreen

class OrganicMotionShader(
    override val name: String = "Organic Motion",
    override val speed: Float = 0.01f,
    override val shader: RuntimeShader = RuntimeShader(
        """
            uniform shader composable;
            uniform float2 resolution;
            uniform float2 pointer;
            uniform float time;
            
            float noise(float2 p) {
                float2 i = floor(p);
                float2 f = fract(p);
                f = f * f * (3.0 - 2.0 * f);
                float a = sin(i.x + i.y * 31.23 + time);
                float b = sin(i.x + 1.0 + i.y * 31.23 + time);
                float c = sin(i.x + (i.y + 1.0) * 31.23 + time);
                float d = sin(i.x + 1.0 + (i.y + 1.0) * 31.23 + time);
                return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
            }
            
            float fbm(float2 p) {
                float sum = 0.0;
                float amp = 1.0;
                float freq = 1.0;
                for(int i = 0; i < 6; i++) {
                    sum += noise(p * freq) * amp;
                    amp *= 0.5;
                    freq *= 2.0;
                    p += float2(3.123, 1.732);
                }
                return sum;
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / resolution.xy;
                float2 aspect = float2(resolution.x/resolution.y, 1.0);
                uv = uv * 2.0 - 1.0;
                uv *= aspect;
                
                float2 pointerInfluence = (pointer * 2.0 - 1.0) * aspect;
                float pointerDist = length(uv - pointerInfluence);
                float pointerEffect = smoothstep(0.5, 0.0, pointerDist);
                
                float t = time * 0.2;
                float2 movement = float2(sin(t * 0.5), cos(t * 0.7));
                
                float n1 = fbm(uv * 3.0 + movement + pointerEffect);
                float n2 = fbm(uv * 2.0 - movement - pointerEffect);
                float n3 = fbm(uv * 4.0 + float2(n1, n2));
                
                float3 col1 = float3(0.2, 0.5, 0.8);
                float3 col2 = float3(0.8, 0.2, 0.5);
                float3 col3 = float3(0.1, 0.8, 0.4);
                
                float3 finalColor = mix(col1, col2, n1);
                finalColor = mix(finalColor, col3, n2 * 0.5);
                finalColor += n3 * 0.2;
                
                finalColor += float3(pointerEffect * 0.2);
                
                return half4(finalColor, 1.0);
            }
        """
    )
) : IShaderScreen

@Preview(showBackground = true)
@Composable
private fun OrganicMotionBackgroundPreview() {
    val shader = OrganicMotionShader()
    CardShader(shader.shader)
}