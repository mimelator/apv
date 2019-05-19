#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D myTexture;
uniform sampler2D texture;

uniform float time;
uniform float alpha;
uniform bool scroll;
uniform bool mix;
uniform vec2 resolution;

varying vec4 vertColor;
varying vec4 vertTexCoord;

#define TILES_COUNT_X 4.0
#define SPEED 4.0

void main() {
	vec4 backCol;

	if (scroll) {
		vec2 pos = gl_FragCoord.xy - vec2(SPEED * time);
  		vec2 p = (resolution - TILES_COUNT_X * pos) / resolution.x;
		vec3 col = texture2D (myTexture, p).xyz;
		backCol = vec4 (col, 1.0);
	} else {
		//flip it
		vec2 st = gl_FragCoord.xy / resolution.xy;
		vec2 flippedTexCoord = vec2(st.s, 1.0 - st.t);
    	backCol = texture2D(myTexture, flippedTexCoord.st);
	}

  	//mix
  	gl_FragColor = vec4(backCol.rgb, alpha);
}