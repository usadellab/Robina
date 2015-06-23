ellipse <- function (center, radius, rotate,
    segments = 360, add = FALSE, xlab = "", ylab = "", las = par("las"),
    col = palette()[2], lwd = 2, lty = 1, ...)
{
	# x' = x cos√∏ + y sin√∏
	# y' = y cos√∏ - x sin√∏
    if (!(is.vector(center) && 2 == length(center)))
        stop("center must be a vector of length 2")
    if (!(is.vector(radius) && 2 == length(radius)))
        stop("radius must be a vector of length 2")

	angles <- (0:segments) * 2 * pi/segments
	rotate <- rotate*pi/180
	ellipse <- cbind(radius[1] * cos(angles),
					 radius[2] * sin(angles))
	if(rotate != 0)
		ellipse <- cbind( ellipse[,1]*cos(rotate) + ellipse[,2]*sin(rotate),
						  ellipse[,2]*cos(rotate) - ellipse[,1]*sin(rotate) )
	ellipse <- cbind(center[1]+ellipse[,1], center[2]+ellipse[,2])
    if (add)
        lines(ellipse, col = col, lwd = lwd, lty = lty, ...)
    else    plot(ellipse, type = "l", xlim = c(-4, 4), ylim = c(-4, 4),
			xlab = "", ylab = "", axes = FALSE, col = col, lwd = lwd,
			lty = lty, ...)
}