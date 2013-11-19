rm(list=ls())
library(BTYD)

coords <- read.csv("C:\\\\Users\\Edward\\Desktop\\coords.csv")

proc.file <- function(input.file, output){

  input <- read.csv(input.file)
  elog <- data.frame(input[,c("Machine.Identifier", 
                              "Date.of.Activity", "Product.Total.Price")])
  names(elog) <- c("cust", "date", "sales")
  elog$date <- as.Date(as.character(elog$date), format = "%Y%m%d")

  invisible(data <- dc.ElogToCbsCbt(elog, per="day"))
  cbs <- data$cal$cbs

# should be run from multiple starts
    params <- pnbd.EstimateParameters(cbs)

# optional - plot vs. input data
#  pnbd.PlotFrequencyInCalibration(params, cbs, censor = 6)

  expected.trans <- pnbd.ConditionalExpectedTransactions(params, 365, cbs[,1], cbs[,2], cbs[,3])

  x <- aggregate(elog$sales, by=list(elog$cust), FUN=length)
  m.x <- aggregate(elog$sales, by=list(elog$cust), FUN=mean)
  x <- x$x
  m.x <- m.x$x
  spend.params <- spend.EstimateParameters(m.x.vector=m.x[m.x > 0], x.vector=x[m.x > 0])
  expected.spend <- spend.expected.value(spend.params, m.x, x)
  clv <- expected.trans * expected.spend

  clv <- data.frame(names(clv), expected.trans, expected.spend, clv)
  names(clv)[1] = "Machine.Identifier"
  total <- unique(merge(clv, coords[,c("Machine.Identifier", "Latitude", "Longitude")], 
                 by.y="Machine.Identifier"))

  write.csv(total, file=output)
  return(total)
}

in.dir <- "C:\\\\Users\\Edward\\Desktop\\amazon_in\\"
out.dir <- "C:\\\\Users\\Edward\\Desktop\\amazon_out\\"
file.list <- list.files(in.dir)
for (file in file.list){
   in.file <- paste(in.dir, file, sep="")
   out.file <- paste(out.dir, file, sep="")

  tryCatch({
   out <- NULL
   out <- proc.file(in.file, out.file)
   #print(unique(out))
   if(is.null(out)){
     sink("C:\\\\Users\\Edward\\Desktop\\amazon_out\\warnings.txt", append=TRUE)
     cat("could not process", in.file, fill=TRUE)
     sink(NULL)
   }
  }, finally={
    next
  })

}

