attribute vec4 a_Position;
attribute vec4 a_color;


varying vec4 v_color;

void main()
{

    v_color = a_color;
    gl_Position = a_Position;
    //指定OPEN GL里面一个点的大小
    gl_PointSize = 20.0;
}