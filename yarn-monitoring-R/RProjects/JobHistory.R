library("rjson")
library("RCurl")
source("TimeBoxes.R")

# It returns all the data of a job with specified jobId and specified history server
# The historyServer is in format "hostname:port"

getJob <- function(jobId, historyServer)
{
	job <- list()
	url<-paste("http://",historyServer,"/ws/v1/history/mapreduce/jobs/",jobId, sep="")
	job$job <- fromJSON(getURL(url,httpheader = c(Accept="application/json")))$job
	url<-paste("http://",historyServer,"/ws/v1/history/mapreduce/jobs/",jobId,"/tasks", sep="")
	job$tasks <- transposeListOfLists(fromJSON(getURL(url,httpheader = c(Accept="application/json")))$tasks$task)
	attempts<-list()
	for(i in 1:length(job$tasks$successfulAttempt))
	{
		attempt<-job$tasks$successfulAttempt[i]
		url<-paste(historyServer, "/ws/v1/history/mapreduce/jobs/",jobId,"/tasks/",job$tasks$id[i], "/attempts/",attempt,sep="")
		attempts[[i]]<-fromJSON(getURL(url,httpheader = c(Accept="application/json")))$taskAttempt
	}
	job$attempts<-transposeListOfLists(attempts)
	class(job)<-"mrjob"
	job
}
getTaskCounters <- function(jobId, historyServer)
{
	result<-list()
	url<-paste("http://",historyServer,"/ws/v1/history/mapreduce/jobs/",jobId,"/tasks", sep="")
	tasks <- transposeListOfLists(fromJSON(getURL(url,httpheader = c(Accept="application/json")))$tasks$task)
	for(i in 1:length(tasks$successfulAttempt))
	{
		attempt<-tasks$successfulAttempt[i]
		url<-paste(historyServer, "/ws/v1/history/mapreduce/jobs/",jobId,"/tasks/",tasks$id[i], "/attempts/",attempt,"/counters",sep="")
		counter<-fromJSON(getURL(url,httpheader = c(Accept="application/json")))$jobTaskAttemptCounters$taskAttemptCounterGroup
		for( j in 1:length(counter))
		{
			groups<-counter[[j]]
			for(g in 1:length(groups$counter))
			{
				key<-paste(tail(strsplit(groups$counterGroupName,"\\.")[[1]],n=1),groups$counter[[g]]$name,sep=".");
				value<-groups$counter[[g]]$value
				if (is.null(result[[key]]))
					result[[key]]<-c(value)
				else
					result[[key]]<-c(result[[key]],value)
			}
		}
	}
	result
}

# This function plots lines for each mapper horizontally. The horizontal axis is the time in ms
plotMapTasksTimes <- function(job)
{
	indices<-which(job$tasks$type=="MAP")
	plotTasksTimesdata(job, indices)
}

# This function plots lines for each red+ucer horizontally. The horizontal axis is the time in ms
plotReduceTasksTimes <- function(job)
{
	indices<-which(job$tasks$type=="REDUCE")
	plotTasksTimesdata(job, indices)
}

# This function plots lines for each mapper and reducer horizontally. The horizontal axis is the time in ms
plotTasksTimesdata <- function(job, indices=1:length(job$tasks$startTime))
{
	times<-cbind(job$tasks$startTime[indices],job$tasks$finishTime[indices])
	sortedtimes<-times[order(times[,1]),]
	toplot<-sortedtimes-sortedtimes[1,1]
	for(i in 1:nrow(toplot))
	{
		if ( i==1)
			plot(rbind(c(toplot[i,1],i),c(toplot[i,2],i)),type="l", xlim=c(0,max(toplot)),ylim=c(0,nrow(toplot)), xlab="time (ms)", ylab="task num")
		else
			lines(rbind(c(toplot[i,1],i),c(toplot[i,2],i)))
	}
}

# This function plots the number of active mappers at every time point when this number changes
plotActiveMappersNumdata <- function(job, replot=FALSE, minTime=NULL)
{
	nums <- getActiveTasksNumdata(job, which(job$tasks$type=="MAP", minTime))
	plotActiveTasksNum(nums, replot=replot)
}

# This function plots the number of active reducers at every time point when this number changes
plotActiveReducersNumdata <- function(job, replot=FALSE, minTime=NULL)
{
	nums <- getActiveTasksNumdata(job, which(job$tasks$type=="REDUCE"), minTime)
	plotActiveTasksNum(nums, replot=replot)

}

# This function return the number of active tasks (mappers or reducers depend on indices) at every time point when this number changes
getActiveTasksNumdata <- function(job, indices=1:length(job$tasks$startTime), minTime=NULL)
{
	times<-rbind(cbind(job$tasks$startTime[indices],rep(1,length(indices))),
 		cbind(job$tasks$finishTime[indices],rep(-1,length(indices))))
	nums <- calcNums(times, minTime)
	nums
}

# This function return the number of active reducers in shuffle at every time point when this number changes
getActiveShufflePhaseReducerNumdata <- function(job, minTime=NULL)
{
	indices<-which(job$tasks$type=="REDUCE")
	times<-rbind(cbind(job$tasks$startTime[indices],rep(1,length(indices))),
 		cbind(job$attempts$shuffleFinishTime,rep(-1,length(job$attempts$shuffleFinishTime))))
	nums <- calcNums(times, minTime)
	nums
}
# This function return the number of active reducers in merge at every time point when this number changes
getActiveMergePhaseReducerNumdata <- function(job, minTime=NULL)
{
	times<-rbind(cbind(job$attempts$shuffleFinishTime,rep(1,length(job$attempts$shuffleFinishTime))),
 		cbind(job$attempts$mergeFinishTime,rep(-1,length(job$attempts$mergeFinishTime))))
	nums <- calcNums(times, minTime)
	nums
}
# This function return the number of active reducers in merge at every time point when this number changes
getActiveReducePhaseReducerNumdatadata <- function(job, minTime=NULL)
{
	indices<-which(job$tasks$type=="REDUCE")
	times<-rbind(cbind(job$attempts$mergeFinishTime,rep(1,length(job$attempts$mergeFinishTime))),
 		cbind(job$tasks$finishTime[indices],rep(-1,length(indices))))
	nums <- calcNums(times, minTime)
	nums
}
# This function plot the number of active tasks (mappers or reducers as two graphs, reducers with phases) 
# at every time point when this number changes
plotActiveMRTasksNumdata <- function(job, relative=TRUE)
{
	if (relative)
	{
		indices<-which(job$tasks$type=="MAP")
		offset <- min(job$tasks$startTime[indices])
	}
	else
		offset<-0

	numsM <- getActiveTasksNumdata(job, which(job$tasks$type=="MAP"),offset)
	numsSP <- getActiveShufflePhaseReducerNumdata(job, offset)
	numsMP <- getActiveMergePhaseReducerNumdata(job, offset)
	numsRP <- getActiveReducePhaseReducerNumdata(job, offset)

	yrange<-range(c(numsM[,2], numsSP[,2], numsMP[,2], numsRP[,2]))
	xrange<-range(c(numsM[,1], numsSP[,1], numsMP[,1], numsRP[,1]))
	plotActiveTasksNum(numsM, replot=FALSE, col="green", xlim=xrange, ylim=yrange)
	plotActiveTasksNum(numsSP, replot=TRUE, col="darkorange")
	plotActiveTasksNum(numsMP, replot=TRUE, col="magenta")
	plotActiveTasksNum(numsRP, replot=TRUE, col="blue")
}

# This function plot the number of active reducers in different phases (shuffle, merge, reduce)at every time point when this number changes
plotActiveReduceTasksNumDetaileddata <- function(job, relative=TRUE)
{
	if (relative)
	{
		indices<-which(job$tasks$type=="REDUCE")
		offset <- min(job$tasks$startTime[indices])
	}
	else
		offset<-0
	numsSP <- getActiveShufflePhaseReducerNumdata(job, offset )
	numsMP <- getActiveMergePhaseReducerNumdata(job, offset )
	numsRP <- getActiveReducePhaseReducerNumdata(job, offset )
	yrange<-range(c(numsSP[,2], numsMP[,2], numsRP[,2]))
	xrange<-range(c(numsSP[,1], numsMP[,1], numsRP[,1]))
	plotActiveTasksNum(numsSP, replot=FALSE, col="darkorange", xlim=xrange, ylim=yrange)
	plotActiveTasksNum(numsMP, replot=TRUE, col="magenta")
	plotActiveTasksNum(numsRP, replot=TRUE, col="blue")
}

# This function calculates the number of tasks if times are given as (time, +-1) pairs
calcNums <- function(times, minTime=NULL)
{
	sortedtimes<-times[order(times[,1]),]
	toplot<-sortedtimes
	if ( is.null(minTime) )
		toplot[,1]<-sortedtimes[,1]-sortedtimes[1,1]
	else
		toplot[,1]<-sortedtimes[,1]-minTime
	nums<-matrix(nrow=0,ncol=2)
	num<-0
	for(i in 1:nrow(toplot))
	{
		num<-num+toplot[i,2]
		nums<-rbind(nums,c(toplot[i,1],num))
	}
	nums
}

# It does the actual plotting or replotting
plotActiveTasksNum <- function(nums, replot=FALSE, col="black", xlim=NULL, ylim=NULL)
{
	if (replot)
		points(nums, type="s", xlab="time (ms)",ylab="number of tasks", col=col)
	else
		plot(nums, type="s", xlab="time (ms)",ylab="number of tasks", col=col, xlim=xlim, ylim=ylim)
}

# This is a helper function that is used while loading the job from historyServer
transposeListOfLists <- function(listoflist)
{
	result<-list()
	for(i in 1:length(listoflist))
	{
		for(j in 1:length(listoflist[[i]]))
		{
			result[[names(listoflist[[i]][j])]]<-c(result[[names(listoflist[[i]][j])]],listoflist[[i]][[j]])
		}	
	}	
	result
}
getnum<-function(names)
{
	substr(names, 5, 6)
}
createTimeBoxData <- function(job, relative=FALSE)
{
  result<-timeboxes()
  attemptindices<-match(job$tasks$successfulAttempt,job$attempts$id)
  mapindices<-which(job$attempts$type[attemptindices]=="MAP")
  reduceindices<-which(job$attempts$type[attemptindices]=="REDUCE")
  if (relative)
 	 minstart<-min(job$attempts$startTime)
  else
	minstart<-0
  for(i in 1:length(mapindices))
  {
  	node<-job$attempts$nodeHttpAddress[mapindices[i]]  
	result<-addBox.timeboxes(result, node, c(job$attempts$startTime[mapindices[i]]-minstart,job$attempts$finishTime[mapindices[i]]-minstart,0,0,0))
  }
  for(i in 1:length(reduceindices))
  {
  	node<-job$attempts$nodeHttpAddress[reduceindices[i]]  
	result<-addBox.timeboxes(result, node, c(0, job$attempts$startTime[reduceindices[i]]-minstart,job$attempts$shuffleFinishTime[i]-minstart,job$attempts$mergeFinishTime[i]-minstart,job$attempts$finishTime[reduceindices[i]]-minstart))
  }
  result
}
