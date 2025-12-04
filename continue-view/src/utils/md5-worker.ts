import SparkMD5 from'spark-md5'


//确保在web worker环境下使用
if (typeof globalThis!== 'undefined') {
  //监听消息
    globalThis.addEventListener('message', (event) => {
       const {file,taskId,blockSize,chunkSize} = event.data
       if(file&&taskId&&blockSize&&chunkSize){
        calculateFileMd5Optimized(file, taskId, blockSize, chunkSize)
       }else {
        globalThis.postMessage({
          type: 'error',
          taskId: taskId || 'unknown',
          error: 'Missing required parameters: file, taskId, blockSize, chunkSize',
        })
      }
    })
}

function calculateFileMd5Optimized(file: File, taskId: string, blockSize: number, chunkSize: number) {
    const totalSize = file.size
    const blocks = Math.ceil(totalSize / blockSize)
    const blockHashes: string[] = Array.from({ length: blocks })
    let processedBytes =0 ;
    let processedBlocks =0;
    //获取cpu
    const cpuCount = Math.max(2, navigator.hardwareConcurrency || 2)
    let activeWorkers = 0
    let nextBlockIndex = 0
  function processBlock(blockIndex: number): Promise<void> {
    return new Promise((resolve,reject) => {
   try {
   const start = blockIndex * blockSize 
   const end = Math.min(start + blockSize, totalSize)
   const block = file.slice(start, end)
   let currentChunk = 0
   const chunks = Math.ceil(block.size / chunkSize)
   const reader = new FileReader()
   const spark = new SparkMD5.ArrayBuffer()
   reader.onerror = function (e: ProgressEvent<FileReader>) {
    console.error(`[Worker] 文件读取错误:`, e)
    globalThis.postMessage({
      type: 'error',
      taskId,
      error: e,
    })
    reject(new Error('FileReader error'))
  }
  reader.onload = function (e: ProgressEvent<FileReader>) {
    try{
      if(e.target?.result){
        spark.append(e.target.result)
        currentChunk++
       processedBytes += (e.target?.result as ArrayBuffer).byteLength || 0
       globalThis.postMessage({
        type: 'progress',
        taskId,
        progress: processedBytes / totalSize,
        processedBytes,
        totalSize,
      })
      if(currentChunk < chunks){
        loadNextChunk()
      }
      else{
        blockHashes[blockIndex] = spark.end()
        processedBlocks++
        resolve()
      }
      }
    }
    catch(error){}
  }
  function loadNextChunk() {
    try{
      const start = currentChunk * chunkSize
      const end = Math.min(start + chunkSize, block.size)
      const chunk = block.slice(start, end)
      reader.readAsArrayBuffer(chunk)
    }
    catch(error){
      reject(error)
    }
  }
  loadNextChunk()
   }
   catch (error) {
    reject(error)
  }
    })
  }
  function scheduleBlocks() {
      while (activeWorkers < cpuCount && nextBlockIndex < blocks) {
        const currentIndex = nextBlockIndex++;
        activeWorkers++;
        processBlock(currentIndex).then(() => {
          activeWorkers--;
          if(processedBlocks >= blocks){
          try{
          const finalSpark = new SparkMD5.ArrayBuffer()
          blockHashes.forEach((hash) => {
            const hashBuffer = new TextEncoder().encode(hash)
            finalSpark.append(hashBuffer)
          })
          const finalMd5 = finalSpark.end()
          globalThis.postMessage({
            type: 'complete',
            taskId,
            md5: finalMd5,
          })
         
         
        }catch (error) {
          console.error(`[Worker] 计算最终 MD5 时出错:`, error)
          globalThis.postMessage({
            type: 'error',
            taskId,
            error,
          })
        }
        }else{
          scheduleBlocks()
         }
        }).catch((error) => {
          activeWorkers--
          console.error(`[Worker] 处理块时出错:`, error)
          globalThis.postMessage({
            type: 'error',
            taskId,
            error,
          })
        })
    
      
     
      }

  }
  // 启动调度器
   scheduleBlocks()
}