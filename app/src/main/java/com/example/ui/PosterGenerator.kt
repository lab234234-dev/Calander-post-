package com.example.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.example.data.BrandProfile
import com.example.data.PosterTemplate
import java.io.File
import java.io.FileOutputStream

object PosterGenerator {

    /**
     * Renders a complete customized poster at high definition (1080x1080 px).
     */
    fun generatePosterBitmap(
        context: Context,
        template: PosterTemplate,
        profile: BrandProfile,
        sloganText: String,
        showBrandName: Boolean,
        showMobile: Boolean,
        showAddress: Boolean,
        showSocial: Boolean,
        showLogo: Boolean,
        showQr: Boolean,
        overlayColorHex: String
    ): Bitmap {
        val size = 1080
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Base Template Background
        val resId = context.resources.getIdentifier(template.drawableResName, "drawable", context.packageName)
        if (resId != 0) {
            val baseDrawable = ContextCompat.getDrawable(context, resId)
            if (baseDrawable != null) {
                baseDrawable.setBounds(0, 0, size, size)
                baseDrawable.draw(canvas)
            } else {
                canvas.drawColor(Color.parseColor("#1A237E"))
            }
        } else {
            // Fallback flat color
            canvas.drawColor(Color.parseColor("#1A237E"))
        }

        // 2. Draw Slogan text with a beautiful translucent backdrop card
        drawSloganText(canvas, sloganText, size)

        // 3. Draw Branding Strip at the bottom
        drawBrandingStrip(
            context = context,
            canvas = canvas,
            profile = profile,
            size = size,
            showBrandName = showBrandName,
            showMobile = showMobile,
            showAddress = showAddress,
            showSocial = showSocial,
            showLogo = showLogo,
            showQr = showQr,
            overlayColorHex = overlayColorHex
        )

        return bitmap
    }

    private fun drawSloganText(canvas: Canvas, text: String, size: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 42f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Word wrap slogan text
        val padding = 100f
        val maxWidth = size - padding * 2
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width < maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        // Limit to maximum 3 lines to avoid cluttering the image
        val finalLines = lines.take(3)
        val lineHeight = 55f
        val totalHeight = finalLines.size * lineHeight

        // Position slogan in upper-middle area (around y = 280 to 450 depending on lines)
        val startY = 320f
        
        // Draw standard backdrop card for slogan text readability
        val backdropPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#A3000000") // Black with 64% transparency
            style = Paint.Style.FILL
        }
        
        val margin = 50f
        val cardRect = RectF(
            margin,
            startY - 60f,
            size - margin,
            startY + totalHeight + 10f
        )
        canvas.drawRoundRect(cardRect, 24f, 24f, backdropPaint)

        // Draw elegant orange/gold border on the backdrop card
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFB300") // Soft gold
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(cardRect, 24f, 24f, borderPaint)

        // Draw individual lines of slogan
        finalLines.forEachIndexed { index, line ->
            canvas.drawText(line, size / 2f, startY + (index * lineHeight), paint)
        }
    }

    private fun drawBrandingStrip(
        context: Context,
        canvas: Canvas,
        profile: BrandProfile,
        size: Int,
        showBrandName: Boolean,
        showMobile: Boolean,
        showAddress: Boolean,
        showSocial: Boolean,
        showLogo: Boolean,
        showQr: Boolean,
        overlayColorHex: String
    ) {
        val stripHeight = 220
        val startY = size - stripHeight

        // Background card of branding strip
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(profile.primaryColorHex) // Brand preferred primary color
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, startY.toFloat(), size.toFloat(), size.toFloat(), bgPaint)

        // Draw subtle accent top line on branding strip
        val accentLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(profile.textColorHex)
            strokeWidth = 5f
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, startY.toFloat(), size.toFloat(), startY.toFloat() + 5f, accentLinePaint)

        // Coordinates & spacing
        var nextLeftX = 40f
        val stripCenterY = startY + (stripHeight / 2f)

        // 1. Draw Programmatic Logo
        if (showLogo) {
            val logoSize = 120f
            val logoRect = RectF(nextLeftX, stripCenterY - (logoSize / 2f), nextLeftX + logoSize, stripCenterY + (logoSize / 2f))
            drawProgrammaticLogo(canvas, logoRect, profile.logoType, profile.textColorHex, profile.brandName.take(2).uppercase())
            nextLeftX += logoSize + 40f
        }

        // 2. Draw Brand Name & Address / Info
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(profile.textColorHex)
            textSize = 42f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(profile.textColorHex).adjustAlpha(0.85f)
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        if (showBrandName) {
            // Draw Brand name
            canvas.drawText(profile.brandName, nextLeftX, stripCenterY - 20f, textPaint)
            
            // Draw Tagline/ShopName right below brand name
            val label = if (profile.shopName.isNotBlank() && profile.shopName != "My Shop") profile.shopName else profile.tagline
            canvas.drawText(label, nextLeftX, stripCenterY + 15f, subTextPaint)
        }

        // 3. Draw Contact Details Column at the bottom row of the strip
        val contactY = startY + stripHeight - 35f
        var currentContactX = nextLeftX

        if (showMobile) {
            canvas.drawText("📞 " + profile.mobileNumber, currentContactX, contactY, subTextPaint)
            currentContactX += subTextPaint.measureText(profile.mobileNumber) + 120f
        }

        if (showSocial && currentContactX < size - 220f) {
            canvas.drawText("🌐 " + profile.socialHandle, currentContactX, contactY, subTextPaint)
        }

        // 4. Draw QR Code on the extreme right
        if (showQr) {
            val qrSize = 130f
            val qrRightX = size - 40f
            val qrRect = RectF(qrRightX - qrSize, stripCenterY - (qrSize / 2f) - 10f, qrRightX, stripCenterY + (qrSize / 2f) - 10f)
            drawMockQRCode(canvas, qrRect, profile.textColorHex)
        }
    }

    private fun drawProgrammaticLogo(canvas: Canvas, rect: RectF, logoType: String, colorHex: String, initials: String) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(colorHex)
            style = Paint.Style.FILL
        }

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(colorHex)
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(colorHex)
            textSize = rect.width() * 0.45f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        when (logoType) {
            "geometric_circle" -> {
                // Outer ring
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2f, borderPaint)
                // Inner solid circle
                paint.color = Color.parseColor(colorHex).adjustAlpha(0.25f)
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.3f, paint)
                // Initials Text
                textPaint.color = Color.parseColor(colorHex)
                canvas.drawText(initials, rect.centerX(), rect.centerY() + (textPaint.textSize / 3f), textPaint)
            }
            "geometric_square" -> {
                canvas.drawRoundRect(rect, 16f, 16f, borderPaint)
                paint.color = Color.parseColor(colorHex).adjustAlpha(0.25f)
                canvas.drawRoundRect(rect, 16f, 16f, paint)
                textPaint.color = Color.parseColor(colorHex)
                canvas.drawText(initials, rect.centerX(), rect.centerY() + (textPaint.textSize / 3f), textPaint)
            }
            "shield" -> {
                val path = Path().apply {
                    moveTo(rect.centerX(), rect.top)
                    lineTo(rect.right, rect.top + rect.height() * 0.3f)
                    lineTo(rect.right, rect.bottom - rect.height() * 0.2f)
                    quadTo(rect.centerX(), rect.bottom + 10f, rect.left, rect.bottom - rect.height() * 0.2f)
                    lineTo(rect.left, rect.top + rect.height() * 0.3f)
                    close()
                }
                canvas.drawPath(path, borderPaint)
                paint.color = Color.parseColor(colorHex).adjustAlpha(0.25f)
                canvas.drawPath(path, paint)
                textPaint.color = Color.parseColor(colorHex)
                canvas.drawText(initials, rect.centerX(), rect.centerY() + (textPaint.textSize / 3f), textPaint)
            }
            else -> { // flower or star style
                canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2f, borderPaint)
                // Draw 4 rotating sub-circles
                paint.color = Color.parseColor(colorHex).adjustAlpha(0.4f)
                val petalRadius = rect.width() / 4.5f
                canvas.drawCircle(rect.centerX() - petalRadius, rect.centerY(), petalRadius, paint)
                canvas.drawCircle(rect.centerX() + petalRadius, rect.centerY(), petalRadius, paint)
                canvas.drawCircle(rect.centerX(), rect.centerY() - petalRadius, petalRadius, paint)
                canvas.drawCircle(rect.centerX(), rect.centerY() + petalRadius, petalRadius, paint)
                textPaint.color = Color.parseColor(colorHex)
                canvas.drawText(initials, rect.centerX(), rect.centerY() + (textPaint.textSize / 3f), textPaint)
            }
        }
    }

    private fun drawMockQRCode(canvas: Canvas, rect: RectF, colorHex: String) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        // Draw white background block for QR Code scannability
        canvas.drawRoundRect(rect, 8f, 8f, paint)

        val qrPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        val pSize = rect.width() / 10f
        val startX = rect.left + pSize
        val startY = rect.top + pSize

        // 1. Draw top-left positioning square
        canvas.drawRect(startX, startY, startX + pSize * 3, startY + pSize * 3, qrPaint)
        paint.color = Color.WHITE
        canvas.drawRect(startX + pSize, startY + pSize, startX + pSize * 2, startY + pSize * 2, paint)

        // 2. Draw top-right positioning square
        qrPaint.color = Color.BLACK
        canvas.drawRect(rect.right - pSize * 4, startY, rect.right - pSize, startY + pSize * 3, qrPaint)
        paint.color = Color.WHITE
        canvas.drawRect(rect.right - pSize * 3, startY + pSize, rect.right - pSize * 2, startY + pSize * 2, paint)

        // 3. Draw bottom-left positioning square
        qrPaint.color = Color.BLACK
        canvas.drawRect(startX, rect.bottom - pSize * 4, startX + pSize * 3, rect.bottom - pSize, qrPaint)
        paint.color = Color.WHITE
        canvas.drawRect(startX + pSize, rect.bottom - pSize * 3, startX + pSize * 2, rect.bottom - pSize * 2, paint)

        // 4. Fill in random mock geometric pixels in the rest of the grid
        qrPaint.color = Color.BLACK
        val randomGrid = arrayOf(
            arrayOf(0, 0, 0, 1, 0, 1, 1),
            arrayOf(1, 0, 1, 0, 1, 0, 0),
            arrayOf(0, 1, 1, 1, 0, 1, 1),
            arrayOf(1, 1, 0, 0, 1, 0, 1),
            arrayOf(0, 1, 0, 1, 1, 1, 0),
            arrayOf(1, 0, 1, 1, 0, 0, 1),
            arrayOf(1, 1, 0, 1, 0, 1, 0)
        )

        for (r in 0..6) {
            for (c in 0..6) {
                if (randomGrid[r][c] == 1) {
                    val px = startX + (c + 1) * pSize
                    val py = startY + (r + 1) * pSize
                    // Avoid overlapping position rings
                    if (!((r < 3 && c < 3) || (r < 3 && c > 3) || (r > 3 && c < 3))) {
                        canvas.drawRect(px, py, px + pSize, py + pSize, qrPaint)
                    }
                }
            }
        }
    }

    private fun Int.adjustAlpha(factor: Float): Int {
        val alpha = Math.round(Color.alpha(this) * factor)
        val red = Color.red(this)
        val green = Color.green(this)
        val blue = Color.blue(this)
        return Color.argb(alpha, red, green, blue)
    }

    /**
     * Saves the rendered poster bitmap to a cache/external directory and returns the File.
     */
    fun saveBitmapToTempFile(context: Context, bitmap: Bitmap, filename: String): File? {
        return try {
            val cachePath = File(context.cacheDir, "posters")
            cachePath.mkdirs()
            val file = File(cachePath, "$filename.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
