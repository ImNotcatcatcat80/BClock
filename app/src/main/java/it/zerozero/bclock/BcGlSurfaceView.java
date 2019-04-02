package it.zerozero.bclock;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BcGlSurfaceView extends GLSurfaceView {

    private final BcGlRenderer renderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float prevTouchX;
    private float prevTouchY;


    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     *
     * @param context
     */
    public BcGlSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        renderer = new BcGlRenderer();
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    /**
     * Implement this method to handle touch screen motion events.
     * <p>
     * If this method is used to detect click actions, it is recommended that
     * the actions be performed by implementing and calling
     * {@link #performClick()}. This will ensure consistent system behavior.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        Log.i("Touch X Y", String.format(Locale.ITALIAN, "X=%.2f  Y=%.2f", x, y));

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - prevTouchX;
                float dy = y - prevTouchY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                renderer.setAngle(
                        renderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        prevTouchX = x;
        prevTouchY = y;
        return true;

    }

    private class BcGlRenderer implements GLSurfaceView.Renderer{
        private Square square;
        public volatile float mAngle;
        private float[] rotationMatrix = new float[16];

        // vPMatrix is an abbreviation for "Model View Projection Matrix"
        private final float[] vPMatrix = new float[16];
        private final float[] projectionMatrix = new float[16];
        private final float[] viewMatrix = new float[16];

        /**
         * Called when the surface is created or recreated.
         * <p>
         * Called when the rendering thread
         * starts and whenever the EGL context is lost. The EGL context will typically
         * be lost when the Android device awakes after going to sleep.
         * <p>
         * Since this method is called at the beginning of rendering, as well as
         * every time the EGL context is lost, this method is a convenient place to put
         * code to create resources that need to be created when the rendering
         * starts, and that need to be recreated when the EGL context is lost.
         * Textures are an example of a resource that you might want to create
         * here.
         * <p>
         * Note that when the EGL context is lost, all OpenGL resources associated
         * with that context will be automatically deleted. You do not need to call
         * the corresponding "glDelete" methods such as glDeleteTextures to
         * manually delete these lost resources.
         * <p>
         *
         * @param gl     the GL interface. Use <code>instanceof</code> to
         *               test if the interface supports GL11 or higher interfaces.
         * @param config the EGLConfig of the created surface. Can be used
         */
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Set the background frame color
            GLES20.glClearColor(0.8f, 0.25f, 0.1f, 1.0f);
            // Initialize square
            square = new Square();
        }

        /**
         * Called when the surface changed size.
         * <p>
         * Called after the surface is created and whenever
         * the OpenGL ES surface size changes.
         * <p>
         * Typically you will set your viewport here. If your camera
         * is fixed then you could also set your projection matrix here:
         * <pre class="prettyprint">
         * void onSurfaceChanged(GL10 gl, int width, int height) {
         *     gl.glViewport(0, 0, width, height);
         *     // for a fixed camera, set the projection too
         *     float ratio = (float) width / height;
         *     gl.glMatrixMode(GL10.GL_PROJECTION);
         *     gl.glLoadIdentity();
         *     gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
         * }
         * </pre>
         *
         * @param gl     the GL interface. Use <code>instanceof</code> to
         *               test if the interface supports GL11 or higher interfaces.
         * @param width
         * @param height
         */
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);

            float ratio = (float) width / height;

            // this projection matrix is applied to object coordinates
            // in the onDrawFrame() method
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        }

        /**
         * Called to draw the current frame.
         * <p>
         * This method is responsible for drawing the current frame.
         * <p>
         * The implementation of this method typically looks like this:
         * <pre class="prettyprint">
         * void onDrawFrame(GL10 gl) {
         *     gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
         *     //... other gl calls to render the scene ...
         * }
         * </pre>
         *
         * @param gl the GL interface. Use <code>instanceof</code> to
         *           test if the interface supports GL11 or higher interfaces.
         */
        @Override
        public void onDrawFrame(GL10 gl) {
            float[] scratch = new float[16];

            // Redraw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            // Calculate the projection and view transformation
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

            // Create a rotation for the triangle
            Matrix.setRotateM(rotationMatrix, 0, mAngle, 0, 0, -1.0f);

            // Combine the rotation matrix with the projection and camera view
            // Note that the vPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

            // Draw shape
            square.draw(scratch);
        }

        public int loadShader(int type, String shaderCode){

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;
        }

        public float getAngle() {
            return mAngle;
        }

        public void setAngle(float angle) {
            mAngle = angle;
        }

    }

    public class Square {

        private FloatBuffer vertexBuffer;
        private ShortBuffer drawListBuffer;
        private final int oGlProgram;

        // number of coordinates per vertex in this array
        final int COORDS_PER_VERTEX = 3;
        float squareCoords[] = {
                -0.5f,  0.5f, 0.0f,   // top left
                -0.5f, -0.5f, 0.0f,   // bottom left
                0.5f, -0.5f, 0.0f,   // bottom right
                0.5f,  0.5f, 0.0f }; // top right

        private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

        // Set color with red, green, blue and alpha (opacity) values
        float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

        private int positionHandle;
        private int colorHandle;

        private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
        private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        private final String vertexShaderCode =
                // This matrix member variable provides a hook to manipulate
                // the coordinates of the objects that use this vertex shader
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        // the matrix must be included as a modifier of gl_Position
                        // Note that the uMVPMatrix factor *must be first* in order
                        // for the matrix multiplication product to be correct.
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";

        private final String fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";

        // Use to access and set the view transformation
        private int vPMatrixHandle;


        public Square() {
            // initialize vertex byte buffer for shape coordinates
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 4 bytes per float)
                    squareCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(squareCoords);
            vertexBuffer.position(0);

            // initialize byte buffer for the draw list
            ByteBuffer dlb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 2 bytes per short)
                    drawOrder.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);

            // compile shaders
            // and add to oGlProgram
            int vertexShader = renderer.loadShader(GLES20.GL_VERTEX_SHADER,
                    vertexShaderCode);
            int fragmentShader = renderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                    fragmentShaderCode);

            // create empty OpenGL ES Program
            oGlProgram = GLES20.glCreateProgram();

            // add the vertex shader to program
            GLES20.glAttachShader(oGlProgram, vertexShader);

            // add the fragment shader to program
            GLES20.glAttachShader(oGlProgram, fragmentShader);

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(oGlProgram);

        }

        public void draw() {
            // Add program to OpenGL ES environment
            GLES20.glUseProgram(oGlProgram);

            // get handle to vertex shader's vPosition member
            positionHandle = GLES20.glGetAttribLocation(oGlProgram, "vPosition");

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(positionHandle);

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);

            // get handle to fragment shader's vColor member
            colorHandle = GLES20.glGetUniformLocation(oGlProgram, "vColor");

            // Set color for drawing the triangle/square
            GLES20.glUniform4fv(colorHandle, 1, color, 0);

            // Draw the triangle
            // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

            // Draw the square
            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES, drawOrder.length,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(positionHandle);
        }

        public void draw(float[] mvpMatrix) {
            // Add program to OpenGL ES environment
            GLES20.glUseProgram(oGlProgram);

            // get handle to vertex shader's vPosition member
            positionHandle = GLES20.glGetAttribLocation(oGlProgram, "vPosition");

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(positionHandle);

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);

            // get handle to fragment shader's vColor member
            colorHandle = GLES20.glGetUniformLocation(oGlProgram, "vColor");

            // Set color for drawing the triangle/square
            GLES20.glUniform4fv(colorHandle, 1, color, 0);

            // Draw the triangle
            // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

            // Draw the square
            // GLES20.glDrawElements(
                    // GLES20.GL_TRIANGLES, drawOrder.length,
                    // GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            /** Disable vertex array
            GLES20.glDisableVertexAttribArray(positionHandle);
            */

            // get handle to shape's transformation matrix
            vPMatrixHandle = GLES20.glGetUniformLocation(oGlProgram, "uMVPMatrix");

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

            // Draw the triangle
            // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

            // Draw the square
            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES, drawOrder.length,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(positionHandle);

        }

        /*
        public void draw(float[] newCoords) {
            squareCoords = newCoords;

            // Add program to OpenGL ES environment
            GLES20.glUseProgram(oGlProgram);

            // get handle to vertex shader's vPosition member
            positionHandle = GLES20.glGetAttribLocation(oGlProgram, "vPosition");

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(positionHandle);

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);

            // get handle to fragment shader's vColor member
            colorHandle = GLES20.glGetUniformLocation(oGlProgram, "vColor");

            // Set color for drawing the triangle/square
            GLES20.glUniform4fv(colorHandle, 1, color, 0);

            // Draw the triangle
            // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

            // Draw the square
            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES, drawOrder.length,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(positionHandle);
        }
        */

        /*
        private final String vertexShaderCode =
                "attribute vec4 vPosition;" +
                        "void main() {" +
                        "  gl_Position = vPosition;" +
                        "}";
        */

    }
}
