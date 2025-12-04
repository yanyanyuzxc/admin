import type { constantRoutes } from "@/router/route"

/**
 * 递归读取 DataTransferItemList 中的所有文件（支持文件夹结构）
 * 自动为每个 File 对象添加 webkitRelativePath 属性
 * 仅在支持 File System Access API 的浏览器下有效
 */
export async function getFilesFromDataTransferItems(items: DataTransferItemList): Promise<File[]> {
    const files: File[] = []
  async function traverse(handle:FileSystemHandle, path:string = ''): Promise<void> {
    if(handle.kind === 'file'){
        const file = await (handle as FileSystemFileHandle).getFile()
        const fileWithPath = new File([file],file.name,{
            type: file.type,
            lastModified: file.lastModified,
        })
        //使用object.defineProperty添加属性,原来的file属性不能被修改(浏览器限制)
        Object.defineProperty(fileWithPath,'webkitRelativePath',{
            value: path + file.name,
            writable: false,
            enumerable: true,
            configurable: true
        })
        files.push(fileWithPath)
    }
    else if(handle.kind === 'directory') {
        for await (let [name,childHandle] of (handle as any).entries()){
            await traverse(childHandle,path + name + '/')
        }
    }

  }


    for(let item of Array.from(items)){
        if(item.kind === 'file'  && 'getAsFileSystemHandle' in item){
            const handle = await (item as any).getAsFileSystemHandle()
            if(handle){
                await traverse(handle,'')
            }
        }
    }
  return files

}


/**
 * 检查当前浏览器是否支持File System Access API
 */
export function isFileSystemAccessAPISupported(): boolean {
  return typeof window !== 'undefined' && 'getAsFileSystemHandle' in DataTransferItem.prototype
}