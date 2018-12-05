precision mediump float;       	// Set the default precision to medium. We don't need as high of a
								// precision in the fragment shader.
uniform vec3 u_LightPos;       	// The position of the light in eye space.
uniform sampler2D u_Texture;    // The input texture.

varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.

// The entry point for our fragment shader.
void main()
{
	// Multiply the color by the diffuse illumination level and texture value to get final output color.
    gl_FragColor = texture2D(u_Texture, v_TexCoordinate);
}
