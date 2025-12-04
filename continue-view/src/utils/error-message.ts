 import { ElMessage,type MessageHandler } from 'element-plus' 

let messageInstance:MessageHandler | null
const errorMessage = (options:any) => {
    if (messageInstance) {
             messageInstance.close();
    }
  messageInstance = ElMessage.error(options);
};

export default errorMessage;