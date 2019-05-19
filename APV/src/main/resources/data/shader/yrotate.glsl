#ifdef GL_ES
precision mediump float;
#endif


uniform sampler2D texture;
uniform vec2 u_resolution;
uniform float time;


void main(){
    vec2 st = gl_FragCoord.xy/u_resolution.xy;

    // To move the cross we move the space
    vec2 translate = vec2(cos(time),sin(time));
    st += translate*0.35;

	gl_FragColor = texture2D(texture, st);
}