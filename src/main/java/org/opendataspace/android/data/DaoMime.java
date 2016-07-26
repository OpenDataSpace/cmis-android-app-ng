package org.opendataspace.android.data;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.object.MimeType;

import java.sql.SQLException;
import java.util.Locale;

public class DaoMime extends DaoBaseSimple<MimeType> {

    private PreparedQuery<MimeType> byExt;
    private SelectArg byExtArg;

    DaoMime(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, MimeType.class);
    }

    public MimeType forFileName(String name) {
        try {
            final int dot = name.lastIndexOf(".".charAt(0));
            final String extension = dot > 0 ? name.substring(dot + 1).toLowerCase(Locale.getDefault()) : "";

            if (byExt == null) {
                byExtArg = new SelectArg();
                byExt = queryBuilder().limit(1L).where().eq(MimeType.FIELD_EXT, byExtArg).prepare();
            }

            byExtArg.setValue(extension);
            CloseableIterator<MimeType> it = iterate(byExt);

            try {
                if (it.hasNext()) {
                    return it.nextThrow();
                }
            } finally {
                it.closeQuietly();
            }

            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (!TextUtils.isEmpty(mime)) {
                return new MimeType(extension, mime);
            }
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        return null;
    }

    public void createDefaults() throws SQLException {
        create(new MimeType("", "application/octet-stream", "binary", null));
        create(new MimeType("3fr", "image/x-raw-hasselblad", "raw", "image"));
        create(new MimeType("3g2", "video/3gpp2", "video", "video"));
        create(new MimeType("3gp2", "video/3gpp2", "video", "video"));
        create(new MimeType("3gp", "video/3gp", "video", "video"));
        create(new MimeType("7z", "application/x-7z-compressed", "archive", "zip"));
        create(new MimeType("ai", "application/illustrator", "image", "image"));
        create(new MimeType("aif", "audio/x-aiff", "audio", "audio"));
        create(new MimeType("aifc", "audio/x-aiff", "audio", "audio"));
        create(new MimeType("aiff", "audio/x-aiff", "audio", "audio"));
        create(new MimeType("air", "application/vnd.adobe.air-application-installer-package+zip", "package", "zip"));
        create(new MimeType("apk", "application/vnd.android.package-archive", "package", "zip"));
        create(new MimeType("arw", "image/x-raw-sony", "raw", "image"));
        create(new MimeType("asf", "video/x-ms-asf", "video", "video"));
        create(new MimeType("asnd", "audio/vnd.adobe.soundbooth", "audio", "audio"));
        create(new MimeType("asr", "video/x-ms-asf", "video", "video"));
        create(new MimeType("asx", "video/x-ms-asf", "video", "video"));
        create(new MimeType("au", "audio/basic", "audio", "audio"));
        create(new MimeType("avi", "video/x-msvideo", "video", "video"));
        create(new MimeType("azw", "application/vnd.amazon.ebook", "ebook", null));
        create(new MimeType("bas", "text/plain", "source", null));
        create(new MimeType("bat", "text/plain", "plain", null));
        create(new MimeType("bin", "application/octet-stream", "binary", null));
        create(new MimeType("bmp", "image/bmp", "image", "image"));
        create(new MimeType("body", "text/html", "html", null));
        create(new MimeType("bz", "application/x-bzip", "archive", "zip"));
        create(new MimeType("bz2", "application/x-bzip2", "archive", "zip"));
        create(new MimeType("c", "text/plain", "source", null));
        create(new MimeType("cab", "application/vnd.ms-cab-compressed", "archive", "zip"));
        create(new MimeType("cer", "application/x-x509-ca-cert", "certificate", null));
        create(new MimeType("cgm", "image/cgm", "image", "image"));
        create(new MimeType("class", "application/java", "source", null));
        create(new MimeType("cmx", "image/x-cmx", "image", "image"));
        create(new MimeType("cr2", "image/x-raw-canon", "raw", "image"));
        create(new MimeType("crw", "image/x-raw-canon", "raw", "image"));
        create(new MimeType("csh", "application/x-csh", "source", null));
        create(new MimeType("css", "text/css", "html", null));
        create(new MimeType("csv", "text/csv", "data", "data"));
        create(new MimeType("dcs", "image/x-raw-kodak", "raw", "image"));
        create(new MimeType("deb", "application/x-debian-package", "package", "zip"));
        create(new MimeType("dng", "image/x-raw-adobe", "raw", "image"));
        create(new MimeType("doc", "application/msword", "msword", "doc"));
        create(new MimeType("doct", "application/vnd.openxmlformats-officedocument.wordprocessingml.template", "msword",
                "doc"));
        create(new MimeType("docm", "application/vnd.ms-word.document.macroenabled.12", "msword", "doc"));
        create(new MimeType("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "msword",
                "doc"));
        create(new MimeType("dot", "application/msword", "msword", "doc"));
        create(new MimeType("dotm", "application/vnd.ms-word.template.macroenabled.12", "msword", "doc"));
        create(new MimeType("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template", "msword",
                "doc"));
        create(new MimeType("drf", "image/x-raw-kodak", "raw", "image"));
        create(new MimeType("dtd", "text/xml", "data", "data"));
        create(new MimeType("dwt", "image/x-dwt", "document", "doc"));
        create(new MimeType("dwg", "image/vnd.dwg", "document", "doc"));
        create(new MimeType("ear", "application/zip", "archive", "zip"));
        create(new MimeType("eml", "message/rfc822", "email", null));
        create(new MimeType("eps", "application/eps", "document", "doc"));
        create(new MimeType("epub", "application/epub+zip", "ebook", null));
        create(new MimeType("exe", "application/octet-stream", "binary", null));
        create(new MimeType("f4v", "video/x-f4v", "video", "video"));
        create(new MimeType("fla", "application/x-fla", "source", null));
        create(new MimeType("flac", "audio/x-flac", "audio", "audio"));
        create(new MimeType("fli", "video/x-fli", "video", "video"));
        create(new MimeType("flv", "video/x-flv", "video", "video"));
        create(new MimeType("ftl", "text/plain", "plain", null));
        create(new MimeType("gif", "image/gif", "image", "image"));
        create(new MimeType("gtar", "application/x-gtar", "archive", "zip"));
        create(new MimeType("gz", "application/x-gzip", "archive", "zip"));
        create(new MimeType("gzip", "application/x-gzip", "archive", "zip"));
        create(new MimeType("h", "text/plain", "source", null));
        create(new MimeType("h261", "video/h261", "video", "video"));
        create(new MimeType("h263", "video/h263", "video", "video"));
        create(new MimeType("h264", "video/h264", "video", "video"));
        create(new MimeType("htm", "text/html", "html", null));
        create(new MimeType("html", "text/html", "html", null));
        create(new MimeType("htt", "text/webviewhtml", "html", null));
        create(new MimeType("ico", "image/x-icon", "image", "image"));
        create(new MimeType("ics", "application/calendar", "calendar", null));
        create(new MimeType("ief", "image/ief", "image", "image"));
        create(new MimeType("ini", "text/plain", "plain", null));
        create(new MimeType("j2k", "image/jp2", "image", "image"));
        create(new MimeType("jar", "application/zip", "archive", "zip"));
        create(new MimeType("java", "text/plain", "source", null));
        create(new MimeType("jp2", "image/jp2", "image", "image"));
        create(new MimeType("jpc", "image/jp2", "image", "image"));
        create(new MimeType("jpf", "image/jp2", "image", "image"));
        create(new MimeType("jpe", "image/jpeg", "image", "image"));
        create(new MimeType("jpeg", "image/jpeg", "image", "image"));
        create(new MimeType("jpg", "image/jpeg", "image", "image"));
        create(new MimeType("jpm", "image/jp2", "image", "image"));
        create(new MimeType("jpx", "image/jp2", "image", "image"));
        create(new MimeType("js", "text/x-javascript", "source", null));
        create(new MimeType("json", "application/json", "data", "data"));
        create(new MimeType("jsp", "text/plain", "plain", null));
        create(new MimeType("k25", "image/x-raw-kodak", "raw", "image"));
        create(new MimeType("kdc", "image/x-raw-kodak", "raw", "image"));
        create(new MimeType("key", "application/vnd.apple.keynote", "document", "doc"));
        create(new MimeType("latex", "application/x-latex", "document", "doc"));
        create(new MimeType("log", "text/plain", "plain", null));
        create(new MimeType("m1v", "video/mpeg", "video", "video"));
        create(new MimeType("m2ts", "video/mp2t", "video", "video"));
        create(new MimeType("m2v", "video/mpeg", "video", "video"));
        create(new MimeType("m3u", "audio/x-mpegurl", "playlist", null));
        create(new MimeType("m4a", "audio/mp4", "audio", "audio"));
        create(new MimeType("m4b", "audio/mp4", "audio", "audio"));
        create(new MimeType("mp4a", "audio/mp4", "audio", "audio"));
        create(new MimeType("m4v", "video/x-m4v", "video", "video"));
        create(new MimeType("man", "application/x-troff-man", "document", "doc"));
        create(new MimeType("md", "text/x-markdown", "source", null));
        create(new MimeType("mdb", "application/x-msaccess", "msaccess", null));
        create(new MimeType("mid", "audio/mid", "audio", "audio"));
        create(new MimeType("mov", "video/quicktime", "video", "video"));
        create(new MimeType("movie", "video/x-sgi-movie", "video", "video"));
        create(new MimeType("mp2", "audio/mpeg", "audio", "audio"));
        create(new MimeType("mp3", "audio/mpeg", "audio", "audio"));
        create(new MimeType("mp4", "video/mp4", "video", "video"));
        create(new MimeType("mp4v", "video/mp4", "video", "video"));
        create(new MimeType("mpe", "video/mpeg", "video", "video"));
        create(new MimeType("mpeg", "video/mpeg", "video", "video"));
        create(new MimeType("mpeg2", "video/mpeg2", "video", "video"));
        create(new MimeType("mpg", "video/mpeg", "video", "video"));
        create(new MimeType("mpg4", "video/mp4", "video", "video"));
        create(new MimeType("mpp", "application/vnd.ms-project", "msproject", null));
        create(new MimeType("mpv2", "video/x-sgi-movie", "video", "video"));
        create(new MimeType("mrw", "image/x-raw-minolta", "raw", "image"));
        create(new MimeType("msg", "application/vnd.ms-outlook", "email", null));
        create(new MimeType("mv", "video/x-sgi-movie", "video", "video"));
        create(new MimeType("mw", "text/mediawiki", "source", null));
        create(new MimeType("numbers", "application/vnd.apple.numbers", "document", "doc"));
        create(new MimeType("nef", "image/x-raw-nikon", "raw", "image"));
        create(new MimeType("nrw", "image/x-raw-nikon", "raw", "image"));
        create(new MimeType("nsf", "application/vnd.lotus-notes", "document", "doc"));
        create(new MimeType("odb", "application/vnd.oasis.opendocument.database", "opendocument", "doc"));
        create(new MimeType("odc", "application/vnd.oasis.opendocument.chart", "opendocument", "doc"));
        create(new MimeType("odf", "application/vnd.oasis.opendocument.formula", "opendocument", "doc"));
        create(new MimeType("odft", "application/vnd.oasis.opendocument.formula-template", "opendocument", "doc"));
        create(new MimeType("odg", "application/vnd.oasis.opendocument.graphics", "opendocument", "doc"));
        create(new MimeType("odi", "application/vnd.oasis.opendocument.image", "opendocument", "doc"));
        create(new MimeType("odm", "application/vnd.oasis.opendocument.text-master", "opendocument", "doc"));
        create(new MimeType("odp", "application/vnd.oasis.opendocument.presentation", "opendocument", "doc"));
        create(new MimeType("ods", "application/vnd.oasis.opendocument.spreadsheet", "opendocument", "doc"));
        create(new MimeType("odt", "application/vnd.oasis.opendocument.text", "opendocument", "doc"));
        create(new MimeType("oga", "audio/ogg", "audio", "audio"));
        create(new MimeType("ogg", "audio/ogg", "audio", "audio"));
        create(new MimeType("ogv", "video/ogg", "video", "video"));
        create(new MimeType("ogx", "application/ogg", "audio", "audio"));
        create(new MimeType("onetoc", "application/onenote", "msnote", null));
        create(new MimeType("onetoc2", "application/onenote", "msnote", null));
        create(new MimeType("onetmp", "application/onenote", "msnote", null));
        create(new MimeType("onepkg", "application/onenote", "msnote", null));
        create(new MimeType("orf", "image/x-raw-olympus", "raw", "image"));
        create(new MimeType("otc", "application/vnd.oasis.opendocument.chart-template", "opendocument", "doc"));
        create(new MimeType("otg", "application/vnd.oasis.opendocument.graphics-template", "opendocument", "doc"));
        create(new MimeType("oth", "application/vnd.oasis.opendocument.text-web", "opendocument", "doc"));
        create(new MimeType("oti", "application/vnd.oasis.opendocument.image-template", "opendocument", "doc"));
        create(new MimeType("otp", "application/vnd.oasis.opendocument.presentation-template", "opendocument", "doc"));
        create(new MimeType("ots", "application/vnd.oasis.opendocument.spreadsheet-template", "opendocument", "doc"));
        create(new MimeType("ott", "application/vnd.oasis.opendocument.text-template", "opendocument", "doc"));
        create(new MimeType("pages", "application/vnd.apple.pages", "document", "doc"));
        create(new MimeType("pbm", "image/x-portable-bitmap", "image", "image"));
        create(new MimeType("pdf", "application/pdf", "document", "doc"));
        create(new MimeType("pef", "image/x-raw-pentax", "raw", "image"));
        create(new MimeType("pgm", "image/x-portable-graymap", "image", "image"));
        create(new MimeType("png", "image/png", "image", "image"));
        create(new MimeType("pnm", "image/x-portable-anymap", "image", "image"));
        create(new MimeType("pot", "application/vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("potm", "application/vnd.ms-powerpoint.template.macroenabled.12", "msppt", "doc"));
        create(new MimeType("potx", "application/vnd.openxmlformats-officedocument.presentationml.template", "msppt",
                "doc"));
        create(new MimeType("ppa", "application/vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("ppam", "application/vnd.ms-powerpoint.addin.macroenabled.12", "msppt", "doc"));
        create(new MimeType("ppm", "image/x-portable-pixmap", "image", "image"));
        create(new MimeType("pps", "application/vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("ppsm", "application/vnd.ms-powerpoint.slideshow.macroenabled.12", "msppt", "doc"));
        create(new MimeType("ppt", "application/vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("pptm", "application/vnd.ms-powerpoint.presentation.macroenabled.12", "msppt", null));
        create(new MimeType("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "msppt", "doc"));
        create(new MimeType("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow", "msppt",
                "doc"));
        create(new MimeType("properties", "text/plain", "plain", null));
        create(new MimeType("ps", "application/postscript", "document", "doc"));
        create(new MimeType("psd", "image/vnd.adobe.photoshop", "image", "image"));
        create(new MimeType("ptx", "image/x-raw-pentax", "raw", "image"));
        create(new MimeType("qt", "application/quicktime", "video", "video"));
        create(new MimeType("qvi", "video/x-msvideo", "video", "video"));
        create(new MimeType("r3d", "image/x-raw-red", "raw", "image"));
        create(new MimeType("ra", "audio/x-pn-realaudio", "audio", "audio"));
        create(new MimeType("raf", "image/x-raw-fuji", "raw", "image"));
        create(new MimeType("ram", "audio/x-pn-realaudio", "audio", "audio"));
        create(new MimeType("rar", "application/x-rar-compressed", "archive", "zip"));
        create(new MimeType("ras", "image/x-cmu-raster", "image", "image"));
        create(new MimeType("rgb", "image/x-rgb", "image", "image"));
        create(new MimeType("rpnm", "image/x-portable-anymap", "image", "image"));
        create(new MimeType("rtf", "application/rtf", "document", "doc"));
        create(new MimeType("rtx", "text/richtext", "document", "doc"));
        create(new MimeType("rw2", "image/x-raw-panasonic", "raw", "image"));
        create(new MimeType("rwl", "image/x-raw-leica", "raw", "image"));
        create(new MimeType("sda", "application/vnd.stardivision.draw", "opendocument", "doc"));
        create(new MimeType("sds", "application/vnd.stardivision.chart", "opendocument", "doc"));
        create(new MimeType("sdc", "application/vnd.stardivision.calc", "opendocument", "doc"));
        create(new MimeType("sdp", "application/vnd.stardivision.impress-packed", "opendocument", "doc"));
        create(new MimeType("sdd", "application/vnd.stardivision.impress", "opendocument", "doc"));
        create(new MimeType("sdw", "application/vnd.stardivision.writer", "opendocument", "doc"));
        create(new MimeType("sgl", "application/vnd.stardivision.writer-global", "opendocument", "doc"));
        create(new MimeType("sh", "application/x-sh", "source", null));
        create(new MimeType("shtml", "text/html", "html", null));
        create(new MimeType("sldm", "application/vnd.ms-powerpoint.slide.macroenabled.12", "msppt", "doc"));
        create(new MimeType("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide", "msppt",
                "doc"));
        create(new MimeType("smf", "application/vnd.stardivision.math", "opendocument", "doc"));
        create(new MimeType("snd", "audio/basic", "audio", "audio"));
        create(new MimeType("spd", "application/samsung_note", "document", "doc"));
        create(new MimeType("spx", "audio/ogg", "audio", "audio"));
        create(new MimeType("sql", "text/plain", "source", null));
        create(new MimeType("sr2", "image/x-raw-sony", "raw", "image"));
        create(new MimeType("srf", "image/x-raw-sony", "raw", "image"));
        create(new MimeType("stc", "application/vnd.sun.xml.calc.template", "opendocument", "doc"));
        create(new MimeType("sti", "application/vnd.sun.xml.impress.template", "opendocument", "doc"));
        create(new MimeType("stw", "application/vnd.sun.xml.writer.template", "opendocument", "doc"));
        create(new MimeType("svg", "image/svg+xml", "image", "image"));
        create(new MimeType("swf", "application/x-shockwave-flash", "video", "video"));
        create(new MimeType("sxc", "application/vnd.sun.xml.calc", "opendocument", "doc"));
        create(new MimeType("sxd", "application/vnd.sun.xml.draw", "opendocument", "doc"));
        create(new MimeType("sxi", "application/vnd.sun.xml.impress", "opendocument", "doc"));
        create(new MimeType("sxw", "application/vnd.sun.xml.writer", "opendocument", "doc"));
        create(new MimeType("tar", "application/x-tar", "archive", "zip"));
        create(new MimeType("tcl", "application/x-tcl", "source", null));
        create(new MimeType("tex", "application/x-tex", "document", "doc"));
        create(new MimeType("texinfo", "application/x-texinfo", "document", "doc"));
        create(new MimeType("texi", "application/x-texinfo", "document", "doc"));
        create(new MimeType("tgz", "application/x-compressed", "archive", "zip"));
        create(new MimeType("tif", "image/tiff", "image", "image"));
        create(new MimeType("tiff", "image/tiff", "image", "image"));
        create(new MimeType("ts", "video/mp2t", "video", "video"));
        create(new MimeType("tsv", "text/tab-separated-values", "data", "data"));
        create(new MimeType("txt", "text/plain", "plain", null));
        create(new MimeType("vcf", "text/x-vcard", "contact", null));
        create(new MimeType("vsd", "application/vnd.visio", "msvisio", null));
        create(new MimeType("war", "application/zip", "archive", "zip"));
        create(new MimeType("wav", "audio/x-wav", "audio", "audio"));
        create(new MimeType("weba", "audio/webm", "audio", "audio"));
        create(new MimeType("webm", "video/webm", "video", "video"));
        create(new MimeType("webp", "image/webp", "image", "image"));
        create(new MimeType("wma", "audio/x-ms-wma", "audio", "audio"));
        create(new MimeType("wmf", "application/x-msmetafile", "image", "image"));
        create(new MimeType("wmv", "video/x-ms-wmv", "video", "video"));
        create(new MimeType("wsdl", "application/wsdl+xml", "source", null));
        create(new MimeType("x3f", "image/x-raw-sigma", "raw", "image"));
        create(new MimeType("xbm", "image/x-xbitmap", "image", "image"));
        create(new MimeType("xdp", "application/vnd.adobe.xdp+xml", "document", "doc"));
        create(new MimeType("xhtml", "application/xhtml+xml", "html", null));
        create(new MimeType("xla", "application/vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xlam", "application/vnd.ms-excel.addin.macroenabled.12", "msexcel", null));
        create(new MimeType("xlc", "application/vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xlm", "application/vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xls", "application/vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xlsb", "application/vnd.ms-excel.sheet.binary.macroenabled.12", "msexcel", "doc"));
        create(new MimeType("xlsm", "application/vnd.ms-excel.sheet.macroenabled.12", "msexcel", "doc"));
        create(new MimeType("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "msexcel",
                "doc"));
        create(new MimeType("xlt", "application/vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xltm", "application/vnd.ms-excel.template.macroenabled.12", "msexcel", "doc"));
        create(new MimeType("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template", "msexcel",
                "doc"));
        create(new MimeType("xlw", "application/vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xpm", "image/x-xpixmap", "image", "image"));
        create(new MimeType("xml", "text/xml", "data", "data"));
        create(new MimeType("xsd", "text/xml", "data", "data"));
        create(new MimeType("xsl", "text/xml", "data", "data"));
        create(new MimeType("xslt", "text/xml", "data", "data"));
        create(new MimeType("z", "application/x-compress", "archive", "zip"));
        create(new MimeType("zip", "application/zip", "archive", "zip"));
    }
}
