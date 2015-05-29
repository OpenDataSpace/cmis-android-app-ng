package org.opendataspace.android.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.object.MimeType;

import java.sql.SQLException;
import java.util.Locale;

public class DaoMime extends DaoBase<MimeType> {

    private PreparedQuery<MimeType> byExt;
    private SelectArg byExtArg;

    DaoMime(ConnectionSource source, ObjectCache cache) throws SQLException {
        super(source, cache, MimeType.class);
    }

    @Override
    protected EventDaoBase<MimeType> createEvent() {
        return null;
    }

    public MimeType forFileName(String name) throws SQLException {
        int dot = name.lastIndexOf(".".charAt(0));
        String extension = dot > 0 ? name.substring(dot + 1).toLowerCase(Locale.getDefault()) : "";

        if (byExt == null) {
            byExtArg = new SelectArg();
            byExt = queryBuilder().where().eq(MimeType.FIELD_EXT, byExtArg).prepare();
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

        return null;
    }

    public void createDefaults() throws SQLException {
        create(new MimeType("", "octet-stream", "binary", null));
        create(new MimeType("3fr", "x-raw-hasselblad", "raw", "image"));
        create(new MimeType("3g2", "3gpp2", "video", "video"));
        create(new MimeType("3gp2", "3gpp2", "video", "video"));
        create(new MimeType("3gp", "3gp", "video", "video"));
        create(new MimeType("7z", "x-7z-compressed", "archive", "zip"));
        create(new MimeType("ai", "illustrator", "image", "image"));
        create(new MimeType("aif", "x-aiff", "audio", "audio"));
        create(new MimeType("aifc", "x-aiff", "audio", "audio"));
        create(new MimeType("aiff", "x-aiff", "audio", "audio"));
        create(new MimeType("air", "vnd.adobe.air-application-installer-package+zip", "package", "zip"));
        create(new MimeType("apk", "vnd.android.package-archive", "package", "zip"));
        create(new MimeType("arw", "x-raw-sony", "raw", "image"));
        create(new MimeType("asf", "x-ms-asf", "video", "video"));
        create(new MimeType("asnd", "vnd.adobe.soundbooth", "audio", "audio"));
        create(new MimeType("asr", "x-ms-asf", "video", "video"));
        create(new MimeType("asx", "x-ms-asf", "video", "video"));
        create(new MimeType("au", "basic", "audio", "audio"));
        create(new MimeType("avi", "x-msvideo", "video", "video"));
        create(new MimeType("azw", "vnd.amazon.ebook", "ebook", null));
        create(new MimeType("bas", "plain", "source", null));
        create(new MimeType("bat", "plain", "plain", null));
        create(new MimeType("bin", "octet-stream", "binary", null));
        create(new MimeType("bmp", "bmp", "image", "image"));
        create(new MimeType("body", "html", "html", null));
        create(new MimeType("bz", "x-bzip", "archive", "zip"));
        create(new MimeType("bz2", "x-bzip2", "archive", "zip"));
        create(new MimeType("c", "plain", "source", null));
        create(new MimeType("cab", "vnd.ms-cab-compressed", "archive", "zip"));
        create(new MimeType("cer", "x-x509-ca-cert", "certificate", null));
        create(new MimeType("cgm", "cgm", "image", "image"));
        create(new MimeType("class", "java", "source", null));
        create(new MimeType("cmx", "x-cmx", "image", "image"));
        create(new MimeType("cr2", "x-raw-canon", "raw", "image"));
        create(new MimeType("crw", "x-raw-canon", "raw", "image"));
        create(new MimeType("csh", "x-csh", "source", null));
        create(new MimeType("css", "css", "html", null));
        create(new MimeType("csv", "csv", "data", "data"));
        create(new MimeType("dcs", "x-raw-kodak", "raw", "image"));
        create(new MimeType("deb", "x-debian-package", "package", "zip"));
        create(new MimeType("dng", "x-raw-adobe", "raw", "image"));
        create(new MimeType("doc", "msword", "msword", "doc"));
        create(new MimeType("doct", "vnd.openxmlformats-officedocument.wordprocessingml.template", "msword", "doc"));
        create(new MimeType("docm", "vnd.ms-word.document.macroenabled.12", "msword", "doc"));
        create(new MimeType("docx", "vnd.openxmlformats-officedocument.wordprocessingml.document", "msword", "doc"));
        create(new MimeType("dot", "msword", "msword", "doc"));
        create(new MimeType("dotm", "vnd.ms-word.template.macroenabled.12", "msword", "doc"));
        create(new MimeType("dotx", "vnd.openxmlformats-officedocument.wordprocessingml.template", "msword", "doc"));
        create(new MimeType("drf", "x-raw-kodak", "raw", "image"));
        create(new MimeType("dtd", "xml", "data", "data"));
        create(new MimeType("dwt", "x-dwt", "document", "doc"));
        create(new MimeType("dwg", "vnd.dwg", "document", "doc"));
        create(new MimeType("ear", "zip", "archive", "zip"));
        create(new MimeType("eml", "rfc822", "email", null));
        create(new MimeType("eps", "eps", "document", "doc"));
        create(new MimeType("epub", "epub+zip", "ebook", null));
        create(new MimeType("exe", "octet-stream", "binary", null));
        create(new MimeType("f4v", "x-f4v", "video", "video"));
        create(new MimeType("fla", "x-fla", "source", null));
        create(new MimeType("flac", "x-flac", "audio", "audio"));
        create(new MimeType("fli", "x-fli", "video", "video"));
        create(new MimeType("flv", "x-flv", "video", "video"));
        create(new MimeType("ftl", "plain", "plain", null));
        create(new MimeType("gif", "gif", "image", "image"));
        create(new MimeType("gtar", "x-gtar", "archive", "zip"));
        create(new MimeType("gz", "x-gzip", "archive", "zip"));
        create(new MimeType("gzip", "x-gzip", "archive", "zip"));
        create(new MimeType("h", "plain", "source", null));
        create(new MimeType("h261", "h261", "video", "video"));
        create(new MimeType("h263", "h263", "video", "video"));
        create(new MimeType("h264", "h264", "video", "video"));
        create(new MimeType("htm", "html", "html", null));
        create(new MimeType("html", "html", "html", null));
        create(new MimeType("htt", "webviewhtml", "html", null));
        create(new MimeType("ico", "x-icon", "image", "image"));
        create(new MimeType("ics", "calendar", "calendar", null));
        create(new MimeType("ief", "ief", "image", "image"));
        create(new MimeType("ini", "plain", "plain", null));
        create(new MimeType("j2k", "jp2", "image", "image"));
        create(new MimeType("jar", "zip", "archive", "zip"));
        create(new MimeType("java", "plain", "source", null));
        create(new MimeType("jp2", "jp2", "image", "image"));
        create(new MimeType("jpc", "jp2", "image", "image"));
        create(new MimeType("jpf", "jp2", "image", "image"));
        create(new MimeType("jpe", "jpeg", "image", "image"));
        create(new MimeType("jpeg", "jpeg", "image", "image"));
        create(new MimeType("jpg", "jpeg", "image", "image"));
        create(new MimeType("jpm", "jp2", "image", "image"));
        create(new MimeType("jpx", "jp2", "image", "image"));
        create(new MimeType("js", "x-javascript", "source", null));
        create(new MimeType("json", "json", "data", "data"));
        create(new MimeType("jsp", "plain", "plain", null));
        create(new MimeType("k25", "x-raw-kodak", "raw", "image"));
        create(new MimeType("kdc", "x-raw-kodak", "raw", "image"));
        create(new MimeType("key", "vnd.apple.keynote", "document", "doc"));
        create(new MimeType("latex", "x-latex", "document", "doc"));
        create(new MimeType("log", "plain", "plain", null));
        create(new MimeType("m1v", "mpeg", "video", "video"));
        create(new MimeType("m2ts", "mp2t", "video", "video"));
        create(new MimeType("m2v", "mpeg", "video", "video"));
        create(new MimeType("m3u", "x-mpegurl", "playlist", null));
        create(new MimeType("m4a", "mp4", "audio", "audio"));
        create(new MimeType("m4b", "mp4", "audio", "audio"));
        create(new MimeType("mp4a", "mp4", "audio", "audio"));
        create(new MimeType("m4v", "x-m4v", "video", "video"));
        create(new MimeType("man", "x-troff-man", "document", "doc"));
        create(new MimeType("md", "x-markdown", "source", null));
        create(new MimeType("mdb", "x-msaccess", "msaccess", null));
        create(new MimeType("mid", "mid", "audio", "audio"));
        create(new MimeType("mov", "quicktime", "video", "video"));
        create(new MimeType("movie", "x-sgi-movie", "video", "video"));
        create(new MimeType("mp2", "mpeg", "audio", "audio"));
        create(new MimeType("mp3", "mpeg", "audio", "audio"));
        create(new MimeType("mp4", "mp4", "video", "video"));
        create(new MimeType("mp4v", "mp4", "video", "video"));
        create(new MimeType("mpe", "mpeg", "video", "video"));
        create(new MimeType("mpeg", "mpeg", "video", "video"));
        create(new MimeType("mpeg2", "mpeg2", "video", "video"));
        create(new MimeType("mpg", "mpeg", "video", "video"));
        create(new MimeType("mpg4", "mp4", "video", "video"));
        create(new MimeType("mpp", "vnd.ms-project", "msproject", null));
        create(new MimeType("mpv2", "x-sgi-movie", "video", "video"));
        create(new MimeType("mrw", "x-raw-minolta", "raw", "image"));
        create(new MimeType("msg", "vnd.ms-outlook", "email", null));
        create(new MimeType("mv", "x-sgi-movie", "video", "video"));
        create(new MimeType("mw", "mediawiki", "source", null));
        create(new MimeType("numbers", "vnd.apple.numbers", "document", "doc"));
        create(new MimeType("nef", "x-raw-nikon", "raw", "image"));
        create(new MimeType("nrw", "x-raw-nikon", "raw", "image"));
        create(new MimeType("nsf", "vnd.lotus-notes", "document", "doc"));
        create(new MimeType("odb", "vnd.oasis.opendocument.database", "opendocument", "doc"));
        create(new MimeType("odc", "vnd.oasis.opendocument.chart", "opendocument", "doc"));
        create(new MimeType("odf", "vnd.oasis.opendocument.formula", "opendocument", "doc"));
        create(new MimeType("odft", "vnd.oasis.opendocument.formula-template", "opendocument", "doc"));
        create(new MimeType("odg", "vnd.oasis.opendocument.graphics", "opendocument", "doc"));
        create(new MimeType("odi", "vnd.oasis.opendocument.image", "opendocument", "doc"));
        create(new MimeType("odm", "vnd.oasis.opendocument.text-master", "opendocument", "doc"));
        create(new MimeType("odp", "vnd.oasis.opendocument.presentation", "opendocument", "doc"));
        create(new MimeType("ods", "vnd.oasis.opendocument.spreadsheet", "opendocument", "doc"));
        create(new MimeType("odt", "vnd.oasis.opendocument.text", "opendocument", "doc"));
        create(new MimeType("oga", "ogg", "audio", "audio"));
        create(new MimeType("ogg", "ogg", "audio", "audio"));
        create(new MimeType("ogv", "ogg", "video", "video"));
        create(new MimeType("ogx", "ogg", "audio", "audio"));
        create(new MimeType("onetoc", "onenote", "msnote", null));
        create(new MimeType("onetoc2", "onenote", "msnote", null));
        create(new MimeType("onetmp", "onenote", "msnote", null));
        create(new MimeType("onepkg", "onenote", "msnote", null));
        create(new MimeType("orf", "x-raw-olympus", "raw", "image"));
        create(new MimeType("otc", "vnd.oasis.opendocument.chart-template", "opendocument", "doc"));
        create(new MimeType("otg", "vnd.oasis.opendocument.graphics-template", "opendocument", "doc"));
        create(new MimeType("oth", "vnd.oasis.opendocument.text-web", "opendocument", "doc"));
        create(new MimeType("oti", "vnd.oasis.opendocument.image-template", "opendocument", "doc"));
        create(new MimeType("otp", "vnd.oasis.opendocument.presentation-template", "opendocument", "doc"));
        create(new MimeType("ots", "vnd.oasis.opendocument.spreadsheet-template", "opendocument", "doc"));
        create(new MimeType("ott", "vnd.oasis.opendocument.text-template", "opendocument", "doc"));
        create(new MimeType("pages", "vnd.apple.pages", "document", "doc"));
        create(new MimeType("pbm", "x-portable-bitmap", "image", "image"));
        create(new MimeType("pdf", "pdf", "document", "doc"));
        create(new MimeType("pef", "x-raw-pentax", "raw", "image"));
        create(new MimeType("pgm", "x-portable-graymap", "image", "image"));
        create(new MimeType("png", "png", "image", "image"));
        create(new MimeType("pnm", "x-portable-anymap", "image", "image"));
        create(new MimeType("pot", "vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("potm", "vnd.ms-powerpoint.template.macroenabled.12", "msppt", "doc"));
        create(new MimeType("potx", "vnd.openxmlformats-officedocument.presentationml.template", "msppt", "doc"));
        create(new MimeType("ppa", "vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("ppam", "vnd.ms-powerpoint.addin.macroenabled.12", "msppt", "doc"));
        create(new MimeType("ppm", "x-portable-pixmap", "image", "image"));
        create(new MimeType("pps", "vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("ppsm", "vnd.ms-powerpoint.slideshow.macroenabled.12", "msppt", "doc"));
        create(new MimeType("ppt", "vnd.ms-powerpoint", "msppt", "doc"));
        create(new MimeType("pptm", "vnd.ms-powerpoint.presentation.macroenabled.12", "msppt", null));
        create(new MimeType("pptx", "vnd.openxmlformats-officedocument.presentationml.presentation", "msppt", "doc"));
        create(new MimeType("ppsx", "vnd.openxmlformats-officedocument.presentationml.slideshow", "msppt", "doc"));
        create(new MimeType("properties", "plain", "plain", null));
        create(new MimeType("ps", "postscript", "document", "doc"));
        create(new MimeType("psd", "vnd.adobe.photoshop", "image", "image"));
        create(new MimeType("ptx", "x-raw-pentax", "raw", "image"));
        create(new MimeType("qt", "quicktime", "video", "video"));
        create(new MimeType("qvi", "x-msvideo", "video", "video"));
        create(new MimeType("r3d", "x-raw-red", "raw", "image"));
        create(new MimeType("ra", "x-pn-realaudio", "audio", "audio"));
        create(new MimeType("raf", "x-raw-fuji", "raw", "image"));
        create(new MimeType("ram", "x-pn-realaudio", "audio", "audio"));
        create(new MimeType("rar", "x-rar-compressed", "archive", "zip"));
        create(new MimeType("ras", "x-cmu-raster", "image", "image"));
        create(new MimeType("rgb", "x-rgb", "image", "image"));
        create(new MimeType("rpnm", "x-portable-anymap", "image", "image"));
        create(new MimeType("rtf", "rtf", "document", "doc"));
        create(new MimeType("rtx", "richtext", "document", "doc"));
        create(new MimeType("rw2", "x-raw-panasonic", "raw", "image"));
        create(new MimeType("rwl", "x-raw-leica", "raw", "image"));
        create(new MimeType("sda", "vnd.stardivision.draw", "opendocument", "doc"));
        create(new MimeType("sds", "vnd.stardivision.chart", "opendocument", "doc"));
        create(new MimeType("sdc", "vnd.stardivision.calc", "opendocument", "doc"));
        create(new MimeType("sdp", "vnd.stardivision.impress-packed", "opendocument", "doc"));
        create(new MimeType("sdd", "vnd.stardivision.impress", "opendocument", "doc"));
        create(new MimeType("sdw", "vnd.stardivision.writer", "opendocument", "doc"));
        create(new MimeType("sgl", "vnd.stardivision.writer-global", "opendocument", "doc"));
        create(new MimeType("sh", "x-sh", "source", null));
        create(new MimeType("shtml", "html", "html", null));
        create(new MimeType("sldm", "vnd.ms-powerpoint.slide.macroenabled.12", "msppt", "doc"));
        create(new MimeType("sldx", "vnd.openxmlformats-officedocument.presentationml.slide", "msppt", "doc"));
        create(new MimeType("smf", "vnd.stardivision.math", "opendocument", "doc"));
        create(new MimeType("snd", "basic", "audio", "audio"));
        create(new MimeType("spd", "samsung_note", "document", "doc"));
        create(new MimeType("spx", "ogg", "audio", "audio"));
        create(new MimeType("sql", "plain", "source", null));
        create(new MimeType("sr2", "x-raw-sony", "raw", "image"));
        create(new MimeType("srf", "x-raw-sony", "raw", "image"));
        create(new MimeType("stc", "vnd.sun.xml.calc.template", "opendocument", "doc"));
        create(new MimeType("sti", "vnd.sun.xml.impress.template", "opendocument", "doc"));
        create(new MimeType("stw", "vnd.sun.xml.writer.template", "opendocument", "doc"));
        create(new MimeType("svg", "svg+xml", "image", "image"));
        create(new MimeType("swf", "x-shockwave-flash", "video", "video"));
        create(new MimeType("sxc", "vnd.sun.xml.calc", "opendocument", "doc"));
        create(new MimeType("sxd", "vnd.sun.xml.draw", "opendocument", "doc"));
        create(new MimeType("sxi", "vnd.sun.xml.impress", "opendocument", "doc"));
        create(new MimeType("sxw", "vnd.sun.xml.writer", "opendocument", "doc"));
        create(new MimeType("tar", "x-tar", "archive", "zip"));
        create(new MimeType("tcl", "x-tcl", "source", null));
        create(new MimeType("tex", "x-tex", "document", "doc"));
        create(new MimeType("texinfo", "x-texinfo", "document", "doc"));
        create(new MimeType("texi", "x-texinfo", "document", "doc"));
        create(new MimeType("tgz", "x-compressed", "archive", "zip"));
        create(new MimeType("tif", "tiff", "image", "image"));
        create(new MimeType("tiff", "tiff", "image", "image"));
        create(new MimeType("ts", "mp2t", "video", "video"));
        create(new MimeType("tsv", "tab-separated-values", "data", "data"));
        create(new MimeType("txt", "plain", "plain", null));
        create(new MimeType("vcf", "x-vcard", "contact", null));
        create(new MimeType("vsd", "vnd.visio", "msvisio", null));
        create(new MimeType("war", "zip", "archive", "zip"));
        create(new MimeType("wav", "x-wav", "audio", "audio"));
        create(new MimeType("weba", "webm", "audio", "audio"));
        create(new MimeType("webm", "webm", "video", "video"));
        create(new MimeType("webp", "webp", "image", "image"));
        create(new MimeType("wma", "x-ms-wma", "audio", "audio"));
        create(new MimeType("wmf", "x-msmetafile", "image", "image"));
        create(new MimeType("wmv", "x-ms-wmv", "video", "video"));
        create(new MimeType("wsdl", "wsdl+xml", "source", null));
        create(new MimeType("x3f", "x-raw-sigma", "raw", "image"));
        create(new MimeType("xbm", "x-xbitmap", "image", "image"));
        create(new MimeType("xdp", "vnd.adobe.xdp+xml", "document", "doc"));
        create(new MimeType("xhtml", "xhtml+xml", "html", null));
        create(new MimeType("xla", "vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xlam", "vnd.ms-excel.addin.macroenabled.12", "msexcel", null));
        create(new MimeType("xlc", "vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xlm", "vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xls", "vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xlsb", "vnd.ms-excel.sheet.binary.macroenabled.12", "msexcel", "doc"));
        create(new MimeType("xlsm", "vnd.ms-excel.sheet.macroenabled.12", "msexcel", "doc"));
        create(new MimeType("xlsx", "vnd.openxmlformats-officedocument.spreadsheetml.sheet", "msexcel", "doc"));
        create(new MimeType("xlt", "vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xltm", "vnd.ms-excel.template.macroenabled.12", "msexcel", "doc"));
        create(new MimeType("xltx", "vnd.openxmlformats-officedocument.spreadsheetml.template", "msexcel", "doc"));
        create(new MimeType("xlw", "vnd.ms-excel", "msexcel", "doc"));
        create(new MimeType("xpm", "x-xpixmap", "image", "image"));
        create(new MimeType("xml", "xml", "data", "data"));
        create(new MimeType("xsd", "xml", "data", "data"));
        create(new MimeType("xsl", "xml", "data", "data"));
        create(new MimeType("xslt", "xml", "data", "data"));
        create(new MimeType("z", "x-compress", "archive", "zip"));
        create(new MimeType("zip", "zip", "archive", "zip"));
    }
}
