var serverProperties = {
 contextPath: "${context.path}",
 tapContext: "${tap.context}",
 localeFilesLocation: "${locale.url}",
 targetListFilesLocation: "${targetlist.url}",
 showExtTap: "${show.ext.tap}",
 showMissingTranslationBox: "${show.missing.translation.box}"
};

if(!window.esasky){
	window.esasky = {}
}
window.esasky.databaseColumnsToHide = ["npix", "esasky_npix", "fov", "pos"];