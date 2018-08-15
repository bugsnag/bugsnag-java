bump:
ifeq ($(MODULE),)
	@$(error MODULE is not defined. Run with `make MODULE=module VERSION=number bump`)
endif
ifeq ($(VERSION),)
	@$(error VERSION is not defined. Run with `make VERSION=number bump`)
endif
	@echo Bumping the version number of $(MODULE) to $(VERSION)
	@sed -i '' "s/version=.*/version=$(VERSION)/" $(MODULE)/gradle.properties
	@sed -i '' "s/NOTIFIER_VERSION = .*;/NOTIFIER_VERSION = \"$(VERSION)\";/"\
	 $(MODULE)/src/main/java/com/bugsnag/Notifier.java


