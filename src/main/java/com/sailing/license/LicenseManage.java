//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sailing.license;

import com.alibaba.fastjson.JSONObject;
import com.sailing.license.cache.CacheManager;
import com.sailing.license.secret.AESUtils;
import com.sailing.license.secret.Base64Utils;
import com.sailing.license.secret.MD5Util;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import javax.crypto.BadPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class LicenseManage {
    private String salt = "-HZXL";
    private String md5SecretKey = "MIICdwIBADANBgkqhkiG9w0B-HZXL";
    public static String licenseName = "license.lic";
    public static String defaultLicenseFilePath = "/opt/license";
    public static String licenseTempName = "license_temp.lic";

    public LicenseManage() {
    }

    public String createLicense(License license) {
        String filePath = this.getLicenseDirPath();
        if (license.getType() == 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(5, 31);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            license.setValidDate(sdf.format(c.getTime()));
        }

        filePath = filePath + "/" + license.getRegistCode() + "-license.lic";
        String secretKey = license.getRegistCode() + this.salt;
        String md5Key = MD5Util.getMD5(secretKey);
        String licence = JSONObject.toJSONString(license);

        try {
            String baseKey = Base64Utils.encode(Arrays.copyOf(md5Key.getBytes("UTF-8"), 16));
            byte[] encryptStr = AESUtils.encryptAES(licence.getBytes("UTF-8"), baseKey);
            this.writeLicense(filePath, encryptStr);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return filePath;
    }

    public License getLicense(String licensePath) throws Exception {
        File file = new File(licensePath);
        if (!file.exists()) {
            throw new Exception("license not find");
        } else {
            String secretKey = this.getRegistCode() + this.salt;
            String md5Key = MD5Util.getMD5(secretKey);

            try {
                byte[] bytes = this.readFile(licensePath);
                String baseKey = Base64Utils.encode(Arrays.copyOf(md5Key.getBytes("UTF-8"), 16));
                byte[] decryptStr = AESUtils.decryptAES(bytes, baseKey);
                return (License)JSONObject.parseObject(new String(decryptStr, "UTF-8"), License.class);
            } catch (BadPaddingException var8) {
                throw new Exception("invalid license");
            } catch (Exception var9) {
                throw var9;
            }
        }
    }

    public License validLicense(String licensePath, boolean cache) {
        License license = null;
        String registCode = (String)CacheManager.get("registCode");
        if (registCode == null || registCode.length() == 0) {
            registCode = this.getRegistCode();
            CacheManager.put("registCode", registCode, 3600000L);
        }

        try {
            if (cache) {
                license = (License)CacheManager.get(registCode);
            } else {
                license = this.getLicense(licensePath);
            }

            if (license == null) {
                license = this.getLicense(licensePath);
                CacheManager.put(registCode, license, 3600000L);
            }

            if (!license.getRegistCode().equals(registCode)) {
                throw new Exception("invalid license");
            }

            if (license.getType() == 2) {
                license.setStatus(1);
            } else {
                String validDate = license.getValidDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(validDate);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(5, 1);
                license.setStatus(System.currentTimeMillis() - c.getTime().getTime() < 0L ? 1 : 2);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            license = new License();
            if (var9.getMessage().contains("license not find")) {
                license.setStatus(0);
            } else if (var9.getMessage().contains("invalid license")) {
                license.setStatus(3);
            } else {
                license.setStatus(4);
            }
        }

        license.setRegistCode(registCode);
        return license;
    }

    public License validLicense(boolean cache) {
        String licenseFilePath = this.getLicenseFilePath();
        return this.validLicense(licenseFilePath, cache);
    }

    public void updateLicenseCache() {
        String licenseFilePath = this.getLicenseFilePath();
        License license = this.validLicense(licenseFilePath, false);
        CacheManager.put(this.getRegistCode(), license, 3600000L);
    }

    private void writeLicense(String str, byte[] data) {
        File file = new File(str);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            os.write(data, 0, data.length);
            os.flush();
        } catch (FileNotFoundException var16) {
            var16.printStackTrace();
            System.out.println("文件没有找到！");
        } catch (IOException var17) {
            var17.printStackTrace();
            System.out.println("写入文件失败！");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                    System.out.println("关闭输出流失败！");
                }
            }

        }

    }

    private byte[] readFile(String strFile) {
        FileInputStream is = null;

        try {
            is = new FileInputStream(strFile);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            byte[] var5 = bytes;
            return var5;
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

        return null;
    }

    public String getLicenseSerialNumber() {
        String licenseSerialNumber = "";

        try {
            String licensePath = this.getLicenseDirPath();
            String licenseSeriaNumberPath = licensePath + File.separator + "serialnumber";
            File licenseSeriaNumberFile = new File(licenseSeriaNumberPath);
            String md5Key = MD5Util.getMD5(this.md5SecretKey);
            if (!licenseSeriaNumberFile.exists()) {
                licenseSerialNumber = UUID.randomUUID().toString().toUpperCase();
                String baseKey = Base64Utils.encode(Arrays.copyOf(md5Key.getBytes("UTF-8"), 16));
                byte[] encryptStr = AESUtils.encryptAES(licenseSerialNumber.getBytes("UTF-8"), baseKey);
                this.writeLicense(licenseSeriaNumberPath, encryptStr);
            }

            BasicFileAttributes attr = Files.readAttributes(Paths.get(licenseSeriaNumberPath), BasicFileAttributes.class);
            long lastAccessTime = attr.lastAccessTime().toMillis();
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            String processorId = hal.getProcessor().getProcessorID();
            if (processorId != null && processorId.length() != 0) {
                processorId = processorId.replaceAll("\\s*", "");
            } else {
                processorId = "E30B05XL00FB23BF";
            }

            licenseSerialNumber = processorId + "-" + lastAccessTime;
        } catch (Exception var12) {
            var12.printStackTrace();
        }

        return licenseSerialNumber;
    }

    public String getLicenseDirPath() {
        String dirPath = defaultLicenseFilePath;
        File dirPathFile = new File(dirPath);
        if (!dirPathFile.exists()) {
            dirPathFile.mkdirs();
        }

        return dirPath;
    }

    public String getLicenseTempFilePath() {
        String tempFilePath = "";

        try {
            tempFilePath = this.getLicenseDirPath() + File.separator + licenseTempName;
        } catch (Exception var3) {
            var3.printStackTrace();
            tempFilePath = defaultLicenseFilePath + File.separator + licenseTempName;
        }

        return tempFilePath;
    }

    public String getLicenseFilePath() {
        String filePath = "";

        try {
            filePath = this.getLicenseDirPath() + File.separator + licenseName;
        } catch (Exception var3) {
            var3.printStackTrace();
            filePath = defaultLicenseFilePath + File.separator + licenseName;
        }

        return filePath;
    }

    public String getRegistCode() {
        String code = this.getLicenseSerialNumber();
        code = MD5Util.getMD5(code.replaceAll("\\s*", ""));
        int number = code.length() / 4;
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < number; ++i) {
            sb.append(code.substring(i * 4, (i + 1) * 4));
            if (i < number - 1) {
                sb.append("-");
            }
        }

        return sb.toString().toUpperCase();
    }
}
