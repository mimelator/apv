#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
	//zoom
	//revisit change this param 8 from .1 to 12
	vec2 mirror = vec2(vertTexCoord.st * 8);
	mirror.s = clamp(mirror.s, 0, textureSize

	//get the color at that position
    vec4 c = texture2D(texture, mirror) * vertColor;
    
    //invert
    c.xyz = 1.0 - c.xyz;
    
    //set it
    gl_FragColor = c;
}