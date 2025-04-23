var serverProperties = {
 contextPath: "${context.path}",
 tapContext: "${tap.context}",
 localeFilesLocation: "${locale.url}",
 targetListFilesLocation: "${targetlist.url}",
 showExtTap: "${show.ext.tap}",
 showEva: "${show.eva}",
 showMissingTranslationBox: "${show.missing.translation.box}",
 showJwstTimeSeries: "${show.jwst.time.series}"
};

if(!window.esasky){
	window.esasky = {}
}
window.esasky.databaseColumnsToHide = ["npix", "esasky_npix", "fov"];