timeboxes<-function()
{
	timebox<-list()
	class(timebox)<-"timeboxes"
	timebox
}

addBox.timeboxes<-function(data, line, box)
{
	if (is.null(data[[line]]))
		data[[line]]<-matrix(box, nrow=1, ncol=length(box))
      else
		data[[line]]<-rbind(data[[line]], box)
	data
}


plot.timeboxes<-function(data, lineGroupNum=length(data), linesPerGroup=1, sortGroups=TRUE, colors=c("green", "darkorange", "magenta", "blue"), ylim=c(0,lineGroupNum+0.5), lineNameConv=identity)
{
  	for(i in 1:length(data))
  	{
		data[[i]]<-data[[i]][sort.list(data[[i]][,1]), ]
  	}
  	if (sortGroups)
 		data<-data[order(names(data))]
	minx=.Machine$double.xmax
	maxx=0
	for(n in 1:length(data))
	{
		mi<-min(data[[n]][data[[n]]>0])
		if (minx > mi )
			minx <- mi
		ma<-max(data[[n]][data[[n]]>0])
		if (maxx < ma )
			maxx <- ma
	}
	plot(1, type="n", xlim=c(minx-0.5,maxx+0.5), ylim=ylim,  xlab="time (ms)",ylab="nodes")
	axis(4,at=(1:length(data))-0.5,labels=nodeconv(names(data)))
	for(i in 1:lineGroupNum)
	{
		lines(c(minx-0.5,maxx+0.5),c(i,i))
	}
	for(n in 1:length(data))
	{
		for(r in 1:nrow(data[[n]]))
		{
			for(c in 1:(ncol(data[[n]])-1))
			{
				col<-colors[c]
				if ( data[[n]][r,c]!=0 && data[[n]][r,c+1]!=0)
					rect(data[[n]][r,c],((n-1)*linesPerGroup+((r-1)%%linesPerGroup))/linesPerGroup,data[[n]][r,c+1],((n-1)*linesPerGroup+((r-1)%%linesPerGroup+1))/linesPerGroup, col=col)
			}
		}
	}
}

#data<-timeboxes()
#data[["node01"]]<-rbind(c(10,20,0,0,0),c(10,34,0,0,0),c(0,30,44,57,65),c(0,30,55,75,82))

#data[[1]]<-rbind(c(10,20,0,0),c(10,34,0,0),c(30,44,57,65),c(30,55,75,82),c(40,70,0,0),c(42,72,0,0))
#data[[2]]<-rbind(c(12,22,0,0),c(15,23,0,0),c(40,55,65,74),c(51,64,77,85))
#data[[3]]<-rbind(c(18,32,0,0))
#plot(data)
