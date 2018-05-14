#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D myTexture;
uniform sampler2D texture;
uniform float alpha;
uniform float angle;
uniform bool rotate;
uniform vec2 resolution;
uniform vec2 myTextureResolution;

varying vec4 vertColor;
varying vec4 vertTexCoord;


mat2 rotate2d(float _angle){
    return mat2(cos(_angle),-sin(_angle),
                sin(_angle),cos(_angle));
}

void main() {

	vec2 st = gl_FragCoord.xy / resolution.xy;

  if (rotate) {
  	// move space from the center to the vec2(0.0)
    st -= vec2(0.5);
    // rotate the space
    st = rotate2d(angle) * st;
    // move it back to the original place
    st += vec2(0.5);
  }

  
    //flip it
    //vec2 flippedTexCoord = vec2(st.s, 1.0 - st.t);
  
  	//get the pixel
  	vec4 backCol = texture2D(myTexture, st.st);
  
  	//get the main image	
  	vec4 col = texture2D(texture, vertTexCoord.st);
  
  	//mix
  	gl_FragColor = vec4(backCol.rgb, alpha) * col;
}