(define (convert-to-indexed-png filename
			    out)
  (let* ((image (car (gimp-file-load RUN-NONINTERACTIVE filename filename)))
	 (drawable (car (gimp-image-get-active-layer image))))
    (gimp-image-convert-indexed image NO-DITHER MAKE-PALETTE 255 0 1 "")
    (file-png-save2 RUN-NONINTERACTIVE image drawable out out
		    0 9 0 0 0 0 0 0 0)
    (gimp-image-delete image)))
