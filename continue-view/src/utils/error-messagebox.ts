import { ElMessageBox } from 'element-plus'


const errorMessagebox = (options:any) => {
     ElMessageBox.close();
     return ElMessageBox.confirm(options)
};

export default errorMessagebox;
