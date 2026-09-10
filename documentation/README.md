# 2P-Kt documentation

Built with [MkDocs Material](https://squidfunk.github.io/mkdocs-material/) (narrative docs, under `docs/`,
organised per the [Divio system](https://docs.divio.com/documentation-system/)) plus
[Dokka](https://kotlinlang.org/docs/dokka-introduction.html) (generated API reference, aggregated across every
module and mounted at `/api/`).

## Building locally

```bash
pip install mkdocs-material
../gradlew assembleSite
```

The combined site (narrative docs + API reference) is written to `build/assembledSite`. To preview just the
narrative docs with live-reload while editing (`build/site` skips the slow Dokka aggregation):

```bash
../gradlew mkdocsBuild && mkdocs serve
```

Diagrams are authored as PlantUML sources under `diagrams/` and rendered to SVG under `docs/assets/diagrams/`
(generated, not committed) as part of the build.
