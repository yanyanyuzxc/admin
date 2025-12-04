 import { ElNotification,type NotificationHandle } from 'element-plus' 

let notificationInstance:NotificationHandle|null
const errorNotification = (options:any) => {
    if (notificationInstance) {
             notificationInstance.close();
    }
  notificationInstance = ElNotification.error(options);
};

export default errorNotification;