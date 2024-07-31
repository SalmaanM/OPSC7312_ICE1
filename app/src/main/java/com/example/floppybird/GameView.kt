import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.example.floppybird.R
import com.example.floppybird.tower
import com.example.floppybird.bird.Bird
import kotlin.concurrent.thread

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var running = false
    private lateinit var gameThread: Thread
    private var bird: Bird
    private val gravity = 0.5f
    private val jumpSpeed = -15f
    private var bgX = 0f
    private val towers = mutableListOf<tower>()
    private var gameOver = false
    private var score = 0

    init {
        holder.addCallback(this)
        bird = Bird(context)
        gameThread = Thread {
            while (running) {
                val canvas: Canvas? = holder.lockCanvas()
                if (canvas != null) {
                    if (!gameOver) {
                        updateGame()
                    }
                    drawGame(canvas)
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        // Initialize towers
        for (i in 0..2) {
            towers.add(tower(context,
                (context.resources.displayMetrics.widthPixels + i * 500).
                toFloat(), (Math.random() *
                        (context.resources.displayMetrics.heightPixels -
                                800)).toInt()))
        }
    }

    private fun updateGame() {
        //Updates the bird's position
        bird.update()

        //Updating the background position
        bgX -= 5
        if (bgX <= -width) {
            bgX = 0f
        }

        // Prevent bird from falling off the screen
        if (bird.y + bird.getBitmap().height > height) {
            bird.y = (height - bird.getBitmap().height).toFloat()
            bird.velocity = 0f
            gameOver = true
        } else if (bird.y < 0) {
            bird.y = 0f
            bird.velocity = 0f
            gameOver = true
        }

        //Updates the towers
        towers.forEach { it.update() }

        //Check for collisions with the towers
        if (checkCollision()) {
            gameOver = true
        } else {
            //Increments the score by 2 if bird passes through the towers
            towers.forEach { tower ->
                if (!tower.passed && tower.x < bird.x) {
                    score += 2
                    tower.passed = true
                }
            }
        }
    }

    private fun checkCollision(): Boolean {
        val birdRect = bird.getRectangle()
        towers.forEach { tower ->
            if (Rect.intersects(birdRect, tower.getTopRectangle())
                || Rect.intersects(birdRect, tower.getBottomRectangle())) {
                return true
            }
        }
        return false
    }

    private fun drawGame(canvas: Canvas) {
        //Clears the canvas
        canvas.drawColor(Color.WHITE)

        //Creates the background
        val bg = BitmapFactory.decodeResource(resources, R.drawable.background)
        canvas.drawBitmap(bg, bgX, 0f, Paint())
        canvas.drawBitmap(bg, bgX + width, 0f, Paint())

        //Creates the bird
        canvas.drawBitmap(bird.getBitmap(), bird.x, bird.y, Paint())

        //Creates the towers
        towers.forEach { it.draw(canvas) }

        //Creates the score and sets the text colour
        val paint = Paint().apply {
            textSize = 50f
            color = Color.WHITE
        }
        canvas.drawText("Score: $score", 50f, 100f, paint)

        //If bird hits tower, display "Game Over" message in red text
        if (gameOver) {
            val gameOverPaint = Paint().apply {
                textSize = 100f
                color = Color.RED
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("Game Over", width / 2f, height
                    / 2f, gameOverPaint)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        running = true
        gameThread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        gameThread.join()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int,
                                width: Int, height: Int) {
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN && !gameOver) {
            bird.flap()
        }
        return true
    }
}
