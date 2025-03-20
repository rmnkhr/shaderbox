package com.shaderbox.ai.comp.shaders

import android.graphics.RuntimeShader
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shaderbox.ai.data.IShaderScreen

class NorthernLightsShader(
    override val name: String = "Northern Lights",
    override val speed: Float = 0.05f,

    override val shader: RuntimeShader = RuntimeShader(
        """
            uniform shader composable;
            uniform float2 resolution;
            uniform float2 pointer;
            uniform float time;

            float hash(float n) {
                return fract(sin(n) * 43758.5453);
            }
            
            // 2D Noise
            float noise(float2 p) {
                float2 i = floor(p);
                float2 f = fract(p);
                f = f * f * (3.0 - 2.0 * f);
                float n = i.x + i.y * 57.0;
                return mix(mix(hash(n), hash(n + 1.0), f.x),
                           mix(hash(n + 57.0), hash(n + 58.0), f.x), f.y);
            }
            
            float fbm(float2 p) {
                float sum = 0.0;
                float amp = 0.5;
                float freq = 1.0;
                
                for(int i = 0; i < 6; i++) {
                    sum += amp * noise(p * freq);
                    amp *= 0.5;
                    freq *= 2.0;
                }
                return sum;
            }
            

            float3 northernLights(float2 uv, float time) {

                float aspect = resolution.x / resolution.y;
                uv.x *= aspect;
                
                
                float pointerInfluence = 0.0;
                float2 pointerPos = pointer;
                pointerPos.x *= aspect;
                float dist = length(uv - pointerPos);
                pointerInfluence = 1.0 - smoothstep(0.0, 0.5, dist);

                float n = fbm(float2(uv.x * 0.5, uv.y * 0.5 - time * 0.05));
                float movement = sin(uv.x * 2.0 + time * 0.2) * 0.1 ;
                
                
                float waves = sin(uv.x * 3.0 + time * 0.1 + n * 2.0) * 0.1 + 
                             sin(uv.x * 7.0 - time * 0.15) * 0.05;
                

                waves += pointerInfluence * 0.1 * sin(dist * 10.0 - time);
                
                // Shift northern lights lower (deeper into the screen)
                float yPos = uv.y + waves + movement - 0.2; // shift down
                
                // Northern lights intensity, now covers down to 0.5 of screen
                float lightsIntensity = smoothstep(0.0, 0.5, yPos) * smoothstep(1.0, 0.6, yPos);
                lightsIntensity *= (0.8 + 0.4 * n);

                lightsIntensity *= (1.0 + pointerInfluence * 0.5);
                
                // Northern lights color
                float3 lightsColor = mix(
                    float3(0.0, 0.3, 0.7),  // Deep blue
                    float3(0.1, 0.8, 0.9),  // Cyan
                    n
                );

                lightsColor = mix(lightsColor, float3(0.1, 0.7, 0.4), fbm(uv * 2.0 + time * 0.1) * 0.5);
                
                return lightsColor * lightsIntensity;
            }
            
            float renderStars(float2 uv, float time) {
                float stars = 0.0;
                float2 starUV = uv * 50.0;
                float2 starID = floor(starUV);
                for(int y = -1; y <= 1; y++) {
                    for(int x = -1; x <= 1; x++) {
                        float2 neighbor = float2(float(x), float(y));
                        float2 point = starID + neighbor;
            
                        float starSeed = hash(point.x + point.y * 500.0);
                        float starSize = 0.01 + 0.05 * pow(hash(point.y + point.x * 500.0), 2.0); // більш м'які і більші
            
                        float2 starCenter = point + float2(
                            hash(point.y + 343.32),
                            hash(point.x + 3343.32)
                        );
            
                        float distToStar = length(starUV - starCenter);
                        float starFalloff = exp(-distToStar * distToStar * 50.0 / starSize);
                        
                        float twinkle = 0.5 + 0.5 * sin(time * 0.5 + starSeed * 20.0 + sin(time * 0.1 + starSeed * 40.0));
            
                        if(starSeed > 0.85) {
                            stars += starFalloff * twinkle * (0.8 + 0.2 * starSeed);
                        }
                    }
                }
                return stars;
            }
            
            half4 main(float2 fragCoord) {
                float2 uv = fragCoord / resolution;
                float aspect = resolution.x / resolution.y;
                float2 adjustedUV = float2(uv.x * aspect, uv.y);


                float3 backgroundColor = mix(
                    float3(0.05, 0.05, 0.15),  // Dark blue at bottom
                    float3(0.0, 0.0, 0.0),  // Black at top
                    smoothstep(0.0, 0.6, uv.y)
                );

                float stars = renderStars(adjustedUV, time);
                
                // Northern lights effect
                float3 northernLightsColor = northernLights(adjustedUV, time);
                float3 finalColor = backgroundColor;
                finalColor += float3(stars) * smoothstep(0.6, 0.3, uv.y);
                finalColor += northernLightsColor;
                return half4(finalColor, 1.0);
            }
            """
    ),
) : IShaderScreen

@Preview(showBackground = true)
@Composable
private fun NorthernLightsPreview() {
    CardShader(NorthernLightsShader().shader)
}