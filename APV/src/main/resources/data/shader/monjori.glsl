#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform vec2 resolution;
uniform float time;
uniform float alpha;
//uniform sampler2D texture;

void main(void) {
  vec2 p = -1.0 + 2.0 * gl_FragCoord.xy / resolution.xy;
  float a = time*40.0;
  float d,e,f,g=1.0/40.0,h,i,r,q;
  e=400.0*(p.x*0.5+0.5);
  f=400.0*(p.y*0.5+0.5);
  i=200.0+sin(e*g+a/150.0)*20.0;
  d=200.0+cos(f*g/2.0)*18.0+cos(e*g)*7.0;
  r=sqrt(pow(i-e,2.0)+pow(d-f,2.0));
  q=f/r;
  e=(r*cos(q))-a/2.0;f=(r*sin(q))-a/2.0;
  d=sin(e*g)*176.0+sin(e*g)*164.0+r;
  h=((f+d)+a/2.0)*g;
  i=cos(h+r*p.x/1.3)*(e+e+a)+cos(q*g*6.0)*(r+h/3.0);
  h=sin(f*g)*144.0-sin(e*g)*212.0*p.x;
  h=(h+(f-e)*q+sin(r-(a+h)/7.0)*10.0+i/4.0)*g;
  i+=cos(h*2.3*sin(a/350.0-q))*184.0*sin(q-(r*4.3+a/12.0)*g)+tan(r*g+h)*184.0*cos(r*g+h);
  i=mod(i/5.6,256.0)/64.0;
  if(i<0.0) i+=4.0;
  if(i>=2.0) i=4.0-i;
  d=r/350.0;
  d+=sin(d*d*8.0)*0.52;
  f=(sin(a*g)+1.0)/2.0;
  gl_FragColor=vec4(vec3(f*i/1.6,i/2.0+d/13.0,i)*d*p.x+vec3(i/1.3+d/8.0,i/2.0+d/18.0,i)*d*(1.0-p.x),alpha);
}

//https://stackoverflow.com/questions/9151238/can-anyone-explain-what-this-glsl-fragment-shader-is-doing
/*
Converted to ShaderToy by Michael Pohoreski

Originally from:
http://www.pouet.net/prod.php?which=52761

Implemented
https://threejs.org/examples/webgl_shader.html

Explanation
http://stackoverflow.com/questions/9151238/can-anyone-explain-what-this-glsl-fragment-shader-is-doing

You'll have to excuse the crappy formatting -- that is the original code.
I'm slowly cleaning it up.

Thanks to Fabrice for suggestions!
*/


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    // gl_FragCoord is the position of the pixel being drawn
    // so this code makes p a value that goes from -1 to +1 
    // x and y
    vec2 p = (2.0 * fragCoord.xy / iResolution.xy) - 1.0;

    // a = the time speed up by 40
    // The 5.0 is to keep ShaderToy at the same speed of the original
    float a = (iTime*5.0) * 40.0;

    // declare a bunch of variables.
    float d,e,f,g=1.0/40.0,h,i,r,q;

    // e goes from 0 to 400 across the screen
    //   = 400 * [(p.x*0.5) + 0.5 ]
    //   = 400 * 0.5 * (p.x + 1)
    //   = 200 * p.x + 200
    // But p.x ranges from -1 to +1
    // e = 0 to 400
    e = 400.0*(p.x*0.5+0.5);

    // f goes from 0 to 400 down the screen
    f = 400.0*(p.y*0.5+0.5);

    // e and f could be simplified as:
    //vec2 ef = 200.0 * (p + 1.0);
    
    // i goes from 200 + or - 20 based
    // on the sin of e * 1/40th + the slowed down time / 150
    // or in other words slow down even more.
    // e * 1/40 means e goes from 0 to 1
    i = 200.0+sin(e*g+a/150.0)*20.0;
    
    // d is 200 + or - 18.0 + or - 7
    // the first +/- is cos of 0.0 to 0.5 down the screen
    // the second +/i is cos of 0.0 to 1.0 across the screen
    d = 200.0+cos(f*g/2.0)*18.0+cos(e*g)*7.0;

    // I'm stopping here. You can probably figure out the rest
    // see answer
//  r=sqrt(pow(i-e,2.0)+pow(d-f,2.0));
    r = length( vec2( i-e, d-f ) );
    q = f/r;

    e = (r*cos(q))-a/2.0;
    f = (r*sin(q))-a/2.0;
    d = sin(e*g)*176.0 + sin(e*g)*164.0 + r;

    h = ((f+d)+a/2.0)*g;
    i = cos(h+r*p.x/1.3)*(e+e+a) + cos(q*g*6.0)*(r+h/3.0);

    h = sin(f*g)*144.0-sin(e*g)*212.0*p.x;
    h = (h+(f-e)*q+sin(r-(a+h)/7.0)*10.0+i/4.0)*g;

//  i += cos(h*2.3*sin(a/350.0-q)) * 184.0*sin(q-(r*4.3+a/12.0)*g) + tan(r*g+h)*184.0*cos(r*g+h);
    i += cos(h*2.3*sin(a/350.0-q)) * 184.0*sin(q-(r*4.3+a/12.0)*g) + sin(r*g+h)*184.0;

    // Split into 4 segments
    i  = mod(i/5.6,256.0)/64.0;

    if (i <  0.0) i += 4.0;
    if (i >= 2.0) i  = 4.0-i;

    d  = r/350.0;
    d += sin(d*d*8.0)*0.52;

    f = (sin(a*g)+1.0)/2.0;
//  fragColor = vec4(vec3(f*i/1.6,i/2.0+d/13.0,i)*d*p.x+vec3(i/1.3+d/8.0,i/2.0+d/18.0,i)*d*(1.0-p.x),1.0);
    fragColor = d * mix(
        vec4( i/1.3+d/8., i/2.+d/18., i, 0) ,
        vec4( f*i/1.6 ,   i/2.+d/13., i, 0) ,
        p.x
    );
}