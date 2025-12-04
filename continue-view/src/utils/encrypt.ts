import { JSEncrypt } from 'jsencrypt'
import CryptoJS from 'crypto-js'
const publicKey
  = 'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM51dgYtMyF+tTQt80sfFOpSV27a7t9u'
  + 'aUVeFrdGiVxscuizE7H8SMntYqfn9lp8a5GH5P1/GGehVjUD2gF/4kcCAwEAAQ=='

  let encryptor: JSEncrypt | null = null;

  export function encryptByRsa(txt: string): string | null {
    //加密防止泄露，以及防止重复加密
    if (!encryptor) {
      encryptor = new JSEncrypt();
      encryptor.setPublicKey(publicKey);
    }
    const encrypted = encryptor.encrypt(txt);
    if (!encrypted) {
      console.error('RSA 加密失败', txt);
      return null;
    }
    return encrypted;
  }
  const defaultKeyWork = 'XwKsGlMcdPMEhR1B'
// aes加密(下去自己了解)
export function encryptByAes(word: string , keyWord = defaultKeyWork) {
  const key = CryptoJS.enc.Utf8.parse(keyWord)
  const arcs = CryptoJS.enc.Utf8.parse(word)
  const encrypted = CryptoJS.AES.encrypt(arcs, key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  })
  return encrypted.toString()
}