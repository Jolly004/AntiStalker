package com.example.antistalker

object StalkerwareDefinitions {
    val KNOWN_PACKAGES = setOf(
        // TheTruthSpy
        "com.apspy.app", "com.fone", "com.guest", "com.ispyoo", "com.ispyoo.traceyou", "com.mxspy",
        "com.spyzee", "com.systemservice", "com.thetruth", "com.ttsapp.catchcheating",
        
        // HelloSpy
        "com.android.innovaspy", "com.example.hellospy", "com.googlesettings.setting", "com.hellospy",
        "com.hellospy.system", "com.maxxspy", "com.maxxspy.system", "com.mobiispy", "com.mobiispy.system",
        "com.mrblue.setting", "com.mrbluetooth.setting", "com.mrtred.setred", "com.prophoto.editor",
        "com.topspy", "com.topspy.system", "com.virsys.tracker", "com.wifiset.service", "com.wifisettings.service",
        "googlesettings.setting",

        // SpyAdvice
        "com.sa.app",

        // Reptilicus
        "com.brot.storage.work", "com.cycle.start.mess", "com.thecybernanny.andapp",
        "net.androidcoreapp.androidbackup", "net.delphiboardlayer.androidcoreapp", "net.reptilicus.clientapp",
        "net.system_updater_abs341", "net.vkurhandler", "se.vkur.clientapp", "yc.sysupd.client",

        // PhoneSheriff
        "com.retina.phonesheriff", "com.retina21.ms41", "com.retina22.ms6", "com.rspl22.retinaspy",
        "com.retinasoft.ephonetracker", "com.rspl15.nanny.android", "com.rspl16.nanny.android",
        "com.rspl17.nanny.android", "com.rspl18.nanny.android", "com.rspl19.nanny.android",
        "com.rspl20.nanny.android", "com.rspl21.nanny.android",

        // OwnSpy
        "com.ownspy.android", "org.system.kernel",

        // Cocospy
        "com.aiyi.admin", "com.cocospy", "com.duiyun.cocospy", "com.duiyun.cocospy.v2",
        "com.duiyun.fonemonitor", "com.duiyun.spyine", "com.duiyun.spyzie", "com.duiyun.spyic",
        "com.minspy.v2", "com.minspy.v3", "com.sc.cocospy.v2", "com.sc.fonemonitor",
        "com.sc.fonemonitor.v2", "com.sc.minspy.v2", "com.sc.neatspy.v2", "com.sc.safespy.v2",
        "com.sc.safespy.v3", "com.sc.spyic.v2", "com.sc.spyic.v3", "com.sc.spyier.v2",
        "com.sc.spyine.v2", "com.sc.spyzie.v2", "com.dy.spyzie.v4", "com.sc.teensafe.v2",
        "com.spyic", "com.wb.production", "com.ws.sc", "com.ws.scli",

        // VIPTrack
        "com.mit.viptrackpro", "com.mit.networkadapter", "com.tag.viptrack",

        // EasyLogger
        "app.EasyLogger", "app.Easylogger", "app.Elogger", "app.childsafetytracker", "app.seniorsafety",

        // Hoverwatch
        "com.android.core.monitor.debug", "com.android.core.monitor.null", "com.android.core.monitornull",
        "com.android.core.monitor", "com.android.core.mnt", "com.android.core.mnta", "com.android.core.mntah",
        "com.android.core.mntb", "com.android.core.mntd", "com.android.core.mnte", "com.android.core.mntf",
        "com.android.core.mntg", "com.android.core.mnth", "com.android.core.mnti", "com.android.core.mntj",
        "com.android.core.mntk", "com.android.core.mntl", "com.android.core.mntm", "com.android.core.mntn",
        "com.android.core.mnto", "com.android.core.mntp", "com.android.core.mntq", "com.android.core.mntr",
        "com.android.core.mnts", "com.android.core.mntt", "com.android.core.mntu", "com.android.core.mntv",
        "com.android.core.mntw", "com.android.core.mntx", "com.android.core.mnty", "com.android.core.mntz",
        "cmf0.c3b5bm90zq.patch",

        // LetMeSpy
        "pl.lidwin.letmespy", "pl.lidwin.letmespy2", "pl.lidwin.letmespy3", "pl.lidwin.letmespy4",
        "pl.lidwin.letmespy5", "pl.lidwin.lms", "pl.lidwin.remote", "pl.lidwin.remote1",
        "pl.lidwin.remote2", "pl.radeal.lms4",

        // Snoopza
        "com.android.core.mngi", "com.android.core.mngj", "com.android.core.mngk", "com.android.core.mngl",
        "com.android.core.mngn", "com.android.core.mngo", "com.android.core.mngp", "com.android.core.mngq",
        "com.android.core.mngr", "com.android.core.mngs", "com.android.core.mngt", "com.android.core.mngu",
        "com.android.core.mngv", "com.android.core.mngw", "com.android.core.mngx", "com.android.core.mngy",
        "com.android.core.mngz",

        // TrackMyPhones
        "com.app.audiorec", "com.app.call_rec_hidden", "com.app.keylogger", "com.app.spy_call_recorder",
        "com.app.recorder", "com.app.videorec", "com.apps.anti_theft", "com.apps.rct.CellTrackerActivity",
        "com.dev4playapps.whatsdeleted", "com.gcm_call_sms_tracker", "com.gcm_call_sms_tracker.updated",
        "com.gcm_call_tracker", "com.gcm_celltracker", "com.local_cell_tracker",
        "com.local_cell_tracker_updated", "com.soh", "com.trackerapps.whatsaptracker",
        "com.trackmyphone_pro", "com.trackmyphones.livefamilytracker",
        "com.trackmyphones.recoverphoneusingchatmessages", "com.trackmyphones.tmpusingchatmessages",

        // FlexiSpy
        "com.vvt.android.syncmanager", "com.telephony.android", "com.fp.backup", "com.android.phone.dialer",

        // Cerberus
        "com.lsdroid.cerberuss", "com.lsdroid.cerberus.persona", "com.lsdroid.cerberus.persona2",
        "com.lsdroid.cerberus.kids", "com.lsdroid.cerberus.client", "com.lsdroid.cerberus",
        "com.surebrec", "com.ssurebrec",

        // mSpy
        "android.helper.system", "android.sys.process", "com.android.keyboardhelper", "com.mspy.lite",
        "core.framework", "com.eyezy.android", "core.update.framework", "med.mspy.mspy",
        "system.framework", "update.service.android", "update.service.android.installer",

        // MeuSpy
        "com.app.com.app.com.app.aplintal", "com.app.insapp2", "com.meuspy", "br.com.cloud.aplicativo",
        "br.com.cloud.backup", "br.com.daggers.gameap", "br.com.daggers.toshtec", "br.com.phonecell.cloud1",
        "br.com.phonecell.go5ge", "br.com.phonecell.maps", "br.com.phonecell.radio",
        "br.com.phonecell.services", "br.com.sistema.aplicativo", "br.com.monsthers.gameap",
        "in.servidor.service", "br.com.galaxys.gameap", "br.com.gamelevel.playstart",
        "br.com.gamelevel.cloudv3", "com.tutorial.instalao", "com.aisistem.instalao",
        "br.com.appfornecedor.legal",

        // AppSpy
        "com.atracker.app", "com.agpstracker.app", "com.aphonetracker.app",
        "com.afreesmstracker.app", "com.mobilefindfree",

        // MobileTrackerFree
        "a.tck.lvmchi", "com.androdid.inteernet.aa21111227", "com.jyotin.ct", "com.lrvciyti.unrxnfig",
        "com.m.service.control", "com.mob.service.control", "com.mobile.gps", "com.mobile.loc",
        "com.mobiletracker", "com.mobiletrackerfree.secondapp", "com.mobiletrackerfree.www",
        "com.mtf.d", "com.netowrk.service", "com.services.phone", "g.google.llc", "m.mob.control",
        "m.mob.service2020", "m.phone.control2020", "m.protect.children", "m.protect.parental",
        "m.secu.children", "m.security.parental", "mob.protect.children", "mob.service.parental2020",
        "mobile.controlparental2020", "mobile.monitor.child2021", "mobile.monitor.child2022",
        "mobile.monitor.child2023", "mobile.monitor.child2034", "mobile.parental2021",
        "mobile.protect.children", "mobile.protect.children2020", "security.mobile.parental",
        "service.download.app", "tracker.mob.gps", "yogaworkouts.dailyyoga.yogafitness",
        "com.get.mtf", "com.mtf.download", "com.protect.download",

        // iKeyMonitor
        "com.android.internet.a20200817", "com.android.internet.a20210916", "com.android.internet.a20220729",
        "com.android.internet.a20220829", "com.android.internet.a20220914",
        "com.sec.android.internet.im.service.im20190118", "com.sec.android.internet.im.service.im20190419",
        "com.sec.android.internet.im.service.im20210815",

        // PanSpy
        "com.panspy.android",

        // AndroidLost
        "com.androidlost", "com.androidlost.smshandler",

        // Metasploit
        "com.metasploit.stage",

        // Spy24
        "net.spy24.wifi", "com.example.openanotherapp", "ir.spy24.updater", "ir.spy24.wifi",
        "app.spy24.systemwifi", "app.spy24.spy24installer",

        // CatWatchful
        "wosc.cwf", "wosc.cwf2", "com.example.wosc.androidclient",

        // HighsterMobile
        "org.secure.smsgps", "com.autoforward.monitor", "com.phonespector.app", "com.ddiutilities.monitor",

        // iMonitorSpy
        "com.imonitor.ainfo", "inc.imonitor",

        // MobileTool
        "org.poleward.burghs.hydrotherapy.homonymously", "org.urates.amirates.suffocate.chiliast",
        "org.connecting.updived.hygeist.interplays",

        // ShadowSpy
        "com.runaki.synclogs", "com.client.requestlogs", "com.shadow.client.android",

        // SpyHuman
        "com.cldprotect", "m.mobile.control", "com.saxfamqvxj", "com.safesecureservice",
        "com.myappspqwddeexo", "com.yurpdpvxnybmlgh", "com.spyhumanrev",

        // uMobix
        "com.tuner.funnelwebview", "com.system.user", "com.play.services",

        // Spymie
        "com.ant.spymie.keylogger",

        // TheOneSpy
        "com.android.services", "com.android.omg",

        // ClevGuard
        "com.kids.pro", "com.kids.whatsapp",

        // EasyPhoneTrack
        "com.spappm_mondow.alarm", "com.monspap.alarm", "com.snapchat.trmonap", "com.snapch.monabcab",

        // bark
        "com.pt.bark",

        // SpyLive360
        "com.sl360", "com.itqredn8dzrl", "com.wifi0", "com.w0f0", "com.w1f1",

        // XNSpy
        "com.system.task", "com.map.system", "com.xnspy.dashboard",

        // MobiSpy
        "com.psac.a.processservice",

        // NeoSpy
        "ns.antapp.module", "com.nsmon.guard",

        // AllTracker
        "city.russ.alltrackercorp", "city.russ.alltrackerfamily", "city.russ.alltrackerinstaller",
        "org.alltracker.security",

        // SpyPhoneApp
        "com.spappm_mondow.alarm", "com.spapptrakapp.alarm", "com.spatrakappp.alarm",

        // AndroidMonitor
        "com.ibm.fb",

        // TalkLog
        "tech.logsettings", "t.tools.app", "technic.settings",

        // SpyMasterPro
        "iqual.calculadora.pro", "com.semantic.childcontrol",

        // FreeAndroidSpy
        "com.hp.vd", "com.hp.vc",

        // NetSpy
        "com.googleplay.settings",

        // Spyier
        "com.sc.spyier.v2",

        // CouplerTracker
        "com.bettertomorrowapps.spyyourlovefree", "com.bytepioneers.coupletracker",

        // GPSTrackerLoki
        "com.mobile.loki", "com.mobile.asgard",

        // SpyApp247
        "com.spyapp247.system",

        // SpyMug
        "com.service.mug",

        // WtSpy
        "com.wwtspy", "com.wtspy.apps",

        // Xnore
        "com.xno.systemservice",

        // EspiaoAndroid
        "com.kfhdha.fkjfgjdi",

        // pcTattletale
        "com.avi.scbase",

        // SpyEra
        "com.wSpyEra",

        // AntiFurtoDroid
        "br.com.maceda.android.antifurtow",

        // CallSMSTracker
        "com.gcm_call_sms_tracker.updated", "com.gizmoquip.smstracker",

        // AiSpyer
        "com.aif.tracksp",

        // SpyToApp
        "com.spytoapp.system",

        // BlurSpy
        "com.saloomughal.spyapp",

        // AppMia
        "com.android.system.devicelogs",

        // SecretCamRecorder
        "com.tools.secretcamcorder",

        // Unisafe
        "ru.usafe.u_safe", "ru.usafe.usafe", "ru.usafe.kid.unisafekids", "su.unisafe.unisafe",

        // TrackView
        "app.cybrook.trackvieo", "app.cybrook.trackviep", "app.cybrook.trackvieq", "app.cybrook.trackvier",
        "app.cybrook.trackvies", "app.cybrook.trackviet", "app.cybrook.trackvieu", "app.cybrook.trackviev",
        "app.cybrook.trackview", "app.cybrook.trackviex", "app.cybrook.trackviey", "app.cybrook.trackviez",
        "app.cybrook.trustserv", "app.lifecircle", "app.trackview", "app.trackview.pro",
        "cn.trackview.shentan", "com.trackview", "cybrook.trackview", "net.cybrook.trackvieo",
        "net.cybrook.trackviep", "net.cybrook.trackvieq", "net.cybrook.trackvier", "net.cybrook.trackvies",
        "net.cybrook.trackviet", "net.cybrook.trackvieu", "net.cybrook.trackviev", "net.cybrook.trackview",
        "net.cybrook.trackviex", "net.cybrook.trackviey", "net.cybrook.trackviez", "net.cybrook.trustserv",
        "net.homesafe", "net.trackview", "net.trackview.pro", "tv.familynk", "tv.familynl", "us.trackview",

        // TrackingSmartphone
        "com.tracking_smartphone", "com.app.remote_control", "com.ts_settings",

        // SpyphoneMobileTracker
        "com.phonetrackerofficial", "com.phonetrackerofficial1",

        // OneLocator
        "mg.locations.track5",

        // RealtimeSpy
        "com.realtime.spyapp",

        // jjspy
        "com.backup.tt",

        // AndroidSpy
        "apk.screenshotrecorder", "apk.keylogger", "apk.kgl", "apk.kwoapsnde",
        "com.as.clipboardmanager", "com.as.facecapture", "com.as.gpstracker", "com.as.keylogger",
        "com.as.keylogger2", "com.as.klogger", "com.as.screenrecorder", "com.as.urllogger",

        // AndroidPolice
        "afs.hbmoczc", "bv.vemzye", "com.amon", "com.monitorchecker", "fod.loqpf", "ifk.ghumlh",
        "mhu.bylbcwc", "oo.ptkqyawh", "sy.slvzccd", "vmf.uxytqgrl", "vn.ehkfqgvn", "yr.tubjypbl",
        "com.dromon", "kenkbltcf.pwpwkvdwmjk", "iiw.orqjtwbkx", "efexmsz.mzuooelftl", "fka.ugsonrqogw",

        // FindMyPhone
        "com.mango.findmyphone", "com.mango.findmyphone2", "com.mango.findmyphone3",

        // Bulgok
        "com.bulgakov.controlphone", "com.bulgakov.bug", "com.bul.b",

        // Tracku
        "com.android.fystem.maps", "com.android.system.maps", "com.google.android.bacfup",
        "com.google.android.safe", "com.wzogle.zndroid.yacfup", "com.qzogle.xndroid.jacfup",
        "com.qzogle.xndroid.jacfut", "com.qzogle.android.jacfut",

        // KidsShield
        "com.protect", "com.aixlunro.uqfhkagb", "com.bzbqbkya.hgozttiu", "com.gzomoyig.qwgawtaz",
        "com.android.inputmethod.latinmy", "com.ntckdlhc.oifhnjwp", "com.selgdg.febgdsra",
        "com.selgdg.mardsdaf", "com.sepfsp.jasend", "com.bnahrrbc.kwexsnhl", "com.tbntxear.vfmkjxme",
        "com.fbhpdsej.gnuebduy", "com.uxgbipup.pdtvcgzc", "com.uzoifhzk.qmqnpwaf",
        "com.zkftwsel.fqnoquuv", "com.mnwkvijy.wzyxgrft", "net.kidlogger.kidlogger",
        "net.teslineservice.kidl5", "net.someapp1.somecorp2", "com.fhekpqbq.otlzonjx",

        // SpyKontrol
        "com.ajygpxjy.bnthtjou", "com.udxlbuno.plwnnhop", "com.igyluazm.iytdhsky",

        // Trackplus
        "com.callhist.calltr", "com.catrsy.jaluc", "com.cellph.montrb", "com.dbzbpr.skt",
        "com.elpatr.woac", "com.ernell.thht", "com.gh.ob", "com.greatdata", "com.kidsmobmon",
        "com.mobitra.todv", "com.mobphn.monit", "com.mobtr.danbel", "com.mophtr.td",
        "com.phone.tracker.smsb", "com.phtranlo.tifach", "com.rephko.stha", "com.s2m",
        "com.s2m.seas", "com.sap4mobile", "com.smart", "com.smartback", "com.smstra.xanris",
        "com.spy2mobile", "com.spy2mobile.light", "com.stmrsa.htxt", "com.tccplos.spth",
        "com.tevi.walpi", "com.tracker.sms.mobile", "com.trackzone.kids", "com.trandmon.tool",
        "com.trphwhat.prob", "com.viewcalls.rem", "com.viewsms.remb", "com.whtrack.monit",

        // WebWatcher
        "com.at.wwka", "com.ati.client", "com.ati.monitor", "com.ati.webwatcherconsole",
        "com.atinc.slcompanion", "com.atiw.wc", "com.awarenesstech.monitor",
        "com.awarenesstech.wwpapp", "com.awarenesstechnologies.sideloadedws", "com.awti.slc",
        "com.screentime", "com.ww.companion",

        // MyCellSpy
        "com.cryzp.leplluln", "com.pser.sysutils", "com.sev.android.systemdev",

        // Spylix
        "com.chaoqi.spyapp",

        // MonitorUltra
        "com.sec.provider.mobile.android",

        // TheWiSpy
        "com.thewispy",

        // Observer
        "YWZiZDFjZTg2NTZlOGI4NDkyYWJjZDJjZDE5ZTM0Mjk.MzkwMmNhZGFiZGZhMjMyZjQzNTJkYmQ1ODg1ZjI1NzA",
        "com.system.settings",

        // Mrecorder
        "com.mobileservices2.synchronization", "com.mrecorder.callrecorder",
        "com.mobileservice.sync", "com.connection.manager",

        // PhoneSpy
        "com.popo.analyse", "com.wlset.info",

        // ShadySpy
        "com.shadyspy.monitor",

        // AbsoluTrack
        "com.ass.antitheft", "com.ass.remotesecurity", "com.ass.ladieschildprotection",
        "com.ots.ladieschildprotection", "com.ots.remotesecurity", "com.ots.antitheft",
        "com.softalogy.thiefguard", "com.ots.womenchildsafety", "com.gss.whereismyphone",
        "com.smart.guardoffline",

        // SmartKeylogger
        "com.AwamiSolution.smartkeylogger",

        // Traccar
        "org.traccar.client", "org.traccar.client.hidden",

        // SpyNote
        "dell.scream.application", "com.spynote.software.stubspynote",

        // FlashKeylogger
        "tej.flashkeylogger", "tej.flashkeyloggerpro", "tej.flashkeylogges",

        // MobiStealth
        "stealthLight.sys", "phone.Secure", "and.LocatorTrial", "and.GuardTrial", "lookOut.Secure",

        // SMSForward
        "one.enix.smsforward",

        // Ahmyth
        "net.droid.talk218", "ahmyth.mine.king.ahmyth",

        // xHunter
        "com.xhunter.client",

        // BosSpy
        "com.android.preference.help.mole",

        // Fenced
        "com.mobilespy.io", "com.fenced.ai",

        // RastreadorDeNamorado
        "br.com.rastreadordenamorado",

        // Trackji
        "com.android.wifi.tracker",

        // XDSpy
        "xd.spy.app",

        // XploitSPY
        "com.remote.app",

        // SpySMS
        "com.devspark.securityotp",

        // DroidWatcher
        "com.droidwatcher",

        // Spyzier
        "com.rana_aditya.child",

        // AndroidSpyApp
        "me.hawkshaw",

        // SpyDroid
        "net.majorkernelpanic.spydroid",

        // SpyAppGhazi
        "com.example.ghazi.sms",

        // Curiosus
        "com.hyadesinc.curiosus",

        // LoveSpy
        "com.example.lovespy.app",

        // ISpy
        "edu.virginia.cs.cs4720.ispy",

        // PatanSpyApp
        "in.spyapp.patanjali.android",

        // Dash
        "com.github.muneebwanee.dash",

        // SpyApp
        "com.femimesusu.libapasopi",

        // MySpyApps
        "com.my.spy.app",

        // OneSpy
        "com.android.system.app", "com.android.settings.app", "seC.fqjx.sqBB",
        "com.qwertyuiop.asdfghjkl",

        // WheresMyDroid
        "com.alienmanfc6.wheresmyandroid",

        // WiseMo
        "com.wisemo.wsmguest.v18", "com.wisemo.host.v10",

        // FindMyKids
        "org.findmykids.app", "org.findmykids.child",

        // BrunoEspiao
        "com.moxlndmp.uhzhgzjh", "com.fzvsdrtb.qamlhxri", "com.thaxygpc.xjsjobpn",
        "com.nhevvtmt.thinkquo",

        // Spyone
        "com.jgeuhcex.ejcxndak", "com.eytewqrm.wvdkgmrl", "com.epbzrqcg.syzzkuqx",
        "com.laucass.phonecontrolenabler", "com.laucass.phonecontroltarget",
        "com.laucass.androsmscontrol", "com.juzyuwqt.thxxnjvf", "com.epasufob.kybavfgt",
        "pl.spyone.agent2", "Aktualizacja.apps",

        // eagleSPY
        "br.com.eaglespy",

        // PhoneMonitor
        "com.pvojpamt.tzzyqjyb", "com.monitor.phone.s0ft.phonemonitor",

        // Buhsam
        "com.google.android.network",

        // AndroRat
        "com.example.reverseshell2"
    )
}
