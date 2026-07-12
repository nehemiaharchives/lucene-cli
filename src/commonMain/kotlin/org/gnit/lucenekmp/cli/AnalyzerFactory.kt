package org.gnit.lucenekmp.cli

import org.gnit.lucenekmp.analysis.Analyzer

val supportedAnalyzerNames: Set<String> = setOf(
    "ArabicAnalyzer",
    "ArmenianAnalyzer",
    "AssameseAnalyzer",
    "BasqueAnalyzer",
    "BelarusianAnalyzer",
    "BengaliAnalyzer",
    "BibleBengaliAnalyzer",
    "BibleEnglishAnalyzer",
    "BibleGermanAnalyzer",
    "BibleHindiAnalyzer",
    "BibleJapaneseAnalyzer",
    "BibleKoreanAnalyzer",
    "BibleMarathiAnalyzer",
    "BibleNepaliAnalyzer",
    "BiblePortugueseAnalyzer",
    "BibleRussianAnalyzer",
    "BibleSpanishAnalyzer",
    "BibleSwedishAnalyzer",
    "BibleTagalogAnalyzer",
    "BibleTamilAnalyzer",
    "BibleTeluguAnalyzer",
    "BibleUkrainianAnalyzer",
    "BibleVietnameseAnalyzer",
    "BrazilianAnalyzer",
    "BulgarianAnalyzer",
    "BurmeseAnalyzer",
    "CatalanAnalyzer",
    "CebuanoAnalyzer",
    "CJKAnalyzer",
    "ClassicAnalyzer",
    "CzechAnalyzer",
    "DanishAnalyzer",
    "DutchAnalyzer",
    "EnglishAnalyzer",
    "EstonianAnalyzer",
    "FinnishAnalyzer",
    "FrenchAnalyzer",
    "GalicianAnalyzer",
    "GermanAnalyzer",
    "GreekAnalyzer",
    "GujaratiAnalyzer",
    "HaitianCreoleAnalyzer",
    "HausaAnalyzer",
    "HindiAnalyzer",
    "HungarianAnalyzer",
    "IgboAnalyzer",
    "IlocanoAnalyzer",
    "IndonesianAnalyzer",
    "IrishAnalyzer",
    "ItalianAnalyzer",
    "JapaneseAnalyzer",
    "JapaneseCompletionAnalyzer",
    "JavaneseAnalyzer",
    "KannadaAnalyzer",
    "KeywordAnalyzer",
    "KhmerAnalyzer",
    "KoreanAnalyzer",
    "LatvianAnalyzer",
    "LithuanianAnalyzer",
    "MalayalamAnalyzer",
    "MalayAnalyzer",
    "MarathiAnalyzer",
    "MorfologikAnalyzer",
    "NepaliAnalyzer",
    "NorwegianAnalyzer",
    "OdiaAnalyzer",
    "PersianAnalyzer",
    "PortugueseAnalyzer",
    "PunjabiAnalyzer",
    "RomanianAnalyzer",
    "RussianAnalyzer",
    "SerbianAnalyzer",
    "SimpleAnalyzer",
    "SinhalaAnalyzer",
    "SmartChineseAnalyzer",
    "SoraniAnalyzer",
    "SpanishAnalyzer",
    "StandardAnalyzer",
    "SundaneseAnalyzer",
    "SwahiliAnalyzer",
    "SwedishAnalyzer",
    "TagalogAnalyzer",
    "TamilAnalyzer",
    "TeluguAnalyzer",
    "ThaiAnalyzer",
    "TigrinyaAnalyzer",
    "TurkishAnalyzer",
    "UAX29URLEmailAnalyzer",
    "UkrainianMorfologikAnalyzer",
    "UnicodeWhitespaceAnalyzer",
    "UrduAnalyzer",
    "UzbekAnalyzer",
    "VietnameseAnalyzer",
    "WhitespaceAnalyzer",
    "YorubaAnalyzer",
)

fun String.toAnalyzer(): Analyzer = when (this) {
    "ArabicAnalyzer" -> org.gnit.lucenekmp.analysis.ar.ArabicAnalyzer()
    "ArmenianAnalyzer" -> org.gnit.lucenekmp.analysis.hy.ArmenianAnalyzer()
    "AssameseAnalyzer" -> org.gnit.lucenekmp.analysis.`as`.AssameseAnalyzer()
    "BasqueAnalyzer" -> org.gnit.lucenekmp.analysis.eu.BasqueAnalyzer()
    "BelarusianAnalyzer" -> org.gnit.lucenekmp.analysis.be.BelarusianAnalyzer()
    "BengaliAnalyzer" -> org.gnit.lucenekmp.analysis.bn.BengaliAnalyzer()
    "BibleBengaliAnalyzer" -> org.gnit.lucenekmp.analysis.bn.ct.BibleBengaliAnalyzer()
    "BibleEnglishAnalyzer" -> org.gnit.lucenekmp.analysis.en.ct.BibleEnglishAnalyzer()
    "BibleGermanAnalyzer" -> org.gnit.lucenekmp.analysis.de.ct.BibleGermanAnalyzer()
    "BibleHindiAnalyzer" -> org.gnit.lucenekmp.analysis.hi.ct.BibleHindiAnalyzer()
    "BibleJapaneseAnalyzer" -> org.gnit.lucenekmp.analysis.ja.ct.BibleJapaneseAnalyzer()
    "BibleKoreanAnalyzer" -> org.gnit.lucenekmp.analysis.ko.ct.BibleKoreanAnalyzer()
    "BibleMarathiAnalyzer" -> org.gnit.lucenekmp.analysis.mr.ct.BibleMarathiAnalyzer()
    "BibleNepaliAnalyzer" -> org.gnit.lucenekmp.analysis.ne.ct.BibleNepaliAnalyzer()
    "BiblePortugueseAnalyzer" -> org.gnit.lucenekmp.analysis.pt.ct.BiblePortugueseAnalyzer()
    "BibleRussianAnalyzer" -> org.gnit.lucenekmp.analysis.ru.ct.BibleRussianAnalyzer()
    "BibleSpanishAnalyzer" -> org.gnit.lucenekmp.analysis.es.ct.BibleSpanishAnalyzer()
    "BibleSwedishAnalyzer" -> org.gnit.lucenekmp.analysis.sv.ct.BibleSwedishAnalyzer()
    "BibleTagalogAnalyzer" -> org.gnit.lucenekmp.analysis.tl.ct.BibleTagalogAnalyzer()
    "BibleTamilAnalyzer" -> org.gnit.lucenekmp.analysis.ta.ct.BibleTamilAnalyzer()
    "BibleTeluguAnalyzer" -> org.gnit.lucenekmp.analysis.te.ct.BibleTeluguAnalyzer()
    "BibleUkrainianAnalyzer" -> org.gnit.lucenekmp.analysis.uk.ct.BibleUkrainianAnalyzer()
    "BibleVietnameseAnalyzer" -> org.gnit.lucenekmp.analysis.vi.ct.BibleVietnameseAnalyzer(org.gnit.lucenekmp.analysis.vi.VietnameseConfig())
    "BrazilianAnalyzer" -> org.gnit.lucenekmp.analysis.br.BrazilianAnalyzer()
    "BulgarianAnalyzer" -> org.gnit.lucenekmp.analysis.bg.BulgarianAnalyzer()
    "BurmeseAnalyzer" -> org.gnit.lucenekmp.analysis.my.BurmeseAnalyzer()
    "CatalanAnalyzer" -> org.gnit.lucenekmp.analysis.ca.CatalanAnalyzer()
    "CebuanoAnalyzer" -> org.gnit.lucenekmp.analysis.ceb.CebuanoAnalyzer()
    "CJKAnalyzer" -> org.gnit.lucenekmp.analysis.cjk.CJKAnalyzer()
    "ClassicAnalyzer" -> org.gnit.lucenekmp.analysis.classic.ClassicAnalyzer()
    "CzechAnalyzer" -> org.gnit.lucenekmp.analysis.cz.CzechAnalyzer()
    "DanishAnalyzer" -> org.gnit.lucenekmp.analysis.da.DanishAnalyzer()
    "DutchAnalyzer" -> org.gnit.lucenekmp.analysis.nl.DutchAnalyzer()
    "EnglishAnalyzer" -> org.gnit.lucenekmp.analysis.en.EnglishAnalyzer()
    "EstonianAnalyzer" -> org.gnit.lucenekmp.analysis.et.EstonianAnalyzer()
    "FinnishAnalyzer" -> org.gnit.lucenekmp.analysis.fi.FinnishAnalyzer()
    "FrenchAnalyzer" -> org.gnit.lucenekmp.analysis.fr.FrenchAnalyzer()
    "GalicianAnalyzer" -> org.gnit.lucenekmp.analysis.gl.GalicianAnalyzer()
    "GermanAnalyzer" -> org.gnit.lucenekmp.analysis.de.GermanAnalyzer()
    "GreekAnalyzer" -> org.gnit.lucenekmp.analysis.el.GreekAnalyzer()
    "GujaratiAnalyzer" -> org.gnit.lucenekmp.analysis.gu.GujaratiAnalyzer()
    "HaitianCreoleAnalyzer" -> org.gnit.lucenekmp.analysis.ht.HaitianCreoleAnalyzer()
    "HausaAnalyzer" -> org.gnit.lucenekmp.analysis.ha.HausaAnalyzer()
    "HindiAnalyzer" -> org.gnit.lucenekmp.analysis.hi.HindiAnalyzer()
    "HungarianAnalyzer" -> org.gnit.lucenekmp.analysis.hu.HungarianAnalyzer()
    "IgboAnalyzer" -> org.gnit.lucenekmp.analysis.ig.IgboAnalyzer()
    "IlocanoAnalyzer" -> org.gnit.lucenekmp.analysis.ilo.IlocanoAnalyzer()
    "IndonesianAnalyzer" -> org.gnit.lucenekmp.analysis.id.IndonesianAnalyzer()
    "IrishAnalyzer" -> org.gnit.lucenekmp.analysis.ga.IrishAnalyzer()
    "ItalianAnalyzer" -> org.gnit.lucenekmp.analysis.it.ItalianAnalyzer()
    "JapaneseAnalyzer" -> org.gnit.lucenekmp.analysis.ja.JapaneseAnalyzer()
    "JapaneseCompletionAnalyzer" -> org.gnit.lucenekmp.analysis.ja.JapaneseCompletionAnalyzer()
    "JavaneseAnalyzer" -> org.gnit.lucenekmp.analysis.jv.JavaneseAnalyzer()
    "KannadaAnalyzer" -> org.gnit.lucenekmp.analysis.kn.KannadaAnalyzer()
    "KeywordAnalyzer" -> org.gnit.lucenekmp.analysis.core.KeywordAnalyzer()
    "KhmerAnalyzer" -> org.gnit.lucenekmp.analysis.km.KhmerAnalyzer()
    "KoreanAnalyzer" -> org.gnit.lucenekmp.analysis.ko.KoreanAnalyzer()
    "LatvianAnalyzer" -> org.gnit.lucenekmp.analysis.lv.LatvianAnalyzer()
    "LithuanianAnalyzer" -> org.gnit.lucenekmp.analysis.lt.LithuanianAnalyzer()
    "MalayalamAnalyzer" -> org.gnit.lucenekmp.analysis.ml.MalayalamAnalyzer()
    "MalayAnalyzer" -> org.gnit.lucenekmp.analysis.ms.MalayAnalyzer()
    "MarathiAnalyzer" -> org.gnit.lucenekmp.analysis.mr.MarathiAnalyzer()
    "MorfologikAnalyzer" -> org.gnit.lucenekmp.analysis.morfologik.MorfologikAnalyzer()
    "NepaliAnalyzer" -> org.gnit.lucenekmp.analysis.ne.NepaliAnalyzer()
    "NorwegianAnalyzer" -> org.gnit.lucenekmp.analysis.no.NorwegianAnalyzer()
    "OdiaAnalyzer" -> org.gnit.lucenekmp.analysis.or.OdiaAnalyzer()
    "PersianAnalyzer" -> org.gnit.lucenekmp.analysis.fa.PersianAnalyzer()
    "PortugueseAnalyzer" -> org.gnit.lucenekmp.analysis.pt.PortugueseAnalyzer()
    "PunjabiAnalyzer" -> org.gnit.lucenekmp.analysis.pa.PunjabiAnalyzer()
    "RomanianAnalyzer" -> org.gnit.lucenekmp.analysis.ro.RomanianAnalyzer()
    "RussianAnalyzer" -> org.gnit.lucenekmp.analysis.ru.RussianAnalyzer()
    "SerbianAnalyzer" -> org.gnit.lucenekmp.analysis.sr.SerbianAnalyzer()
    "SimpleAnalyzer" -> org.gnit.lucenekmp.analysis.core.SimpleAnalyzer()
    "SinhalaAnalyzer" -> org.gnit.lucenekmp.analysis.si.SinhalaAnalyzer()
    "SmartChineseAnalyzer" -> org.gnit.lucenekmp.analysis.cn.smart.SmartChineseAnalyzer()
    "SoraniAnalyzer" -> org.gnit.lucenekmp.analysis.ckb.SoraniAnalyzer()
    "SpanishAnalyzer" -> org.gnit.lucenekmp.analysis.es.SpanishAnalyzer()
    "StandardAnalyzer" -> org.gnit.lucenekmp.analysis.standard.StandardAnalyzer()
    "SundaneseAnalyzer" -> org.gnit.lucenekmp.analysis.su.SundaneseAnalyzer()
    "SwahiliAnalyzer" -> org.gnit.lucenekmp.analysis.sw.SwahiliAnalyzer()
    "SwedishAnalyzer" -> org.gnit.lucenekmp.analysis.sv.SwedishAnalyzer()
    "TagalogAnalyzer" -> org.gnit.lucenekmp.analysis.tl.TagalogAnalyzer()
    "TamilAnalyzer" -> org.gnit.lucenekmp.analysis.ta.TamilAnalyzer()
    "TeluguAnalyzer" -> org.gnit.lucenekmp.analysis.te.TeluguAnalyzer()
    "ThaiAnalyzer" -> org.gnit.lucenekmp.analysis.th.ThaiAnalyzer()
    "TigrinyaAnalyzer" -> org.gnit.lucenekmp.analysis.ti.TigrinyaAnalyzer()
    "TurkishAnalyzer" -> org.gnit.lucenekmp.analysis.tr.TurkishAnalyzer()
    "UAX29URLEmailAnalyzer" -> org.gnit.lucenekmp.analysis.email.UAX29URLEmailAnalyzer()
    "UkrainianMorfologikAnalyzer" -> org.gnit.lucenekmp.analysis.uk.UkrainianMorfologikAnalyzer()
    "UnicodeWhitespaceAnalyzer" -> org.gnit.lucenekmp.analysis.core.UnicodeWhitespaceAnalyzer()
    "UrduAnalyzer" -> org.gnit.lucenekmp.analysis.ur.UrduAnalyzer()
    "UzbekAnalyzer" -> org.gnit.lucenekmp.analysis.uz.UzbekAnalyzer()
    "VietnameseAnalyzer" -> org.gnit.lucenekmp.analysis.vi.VietnameseAnalyzer(org.gnit.lucenekmp.analysis.vi.VietnameseConfig())
    "WhitespaceAnalyzer" -> org.gnit.lucenekmp.analysis.core.WhitespaceAnalyzer()
    "YorubaAnalyzer" -> org.gnit.lucenekmp.analysis.yo.YorubaAnalyzer()
    else -> throw IllegalArgumentException(
        "$this is not supported. Available analyzers: " + supportedAnalyzerNames.joinToString()
    )
}
