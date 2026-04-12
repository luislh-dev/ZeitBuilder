# Contributing to ZeitBuilder

First off, thank you for considering contributing to ZeitBuilder! It's people like you that make ZeitBuilder such a great tool for the JetBrains community.

## 🌿 Branching Strategy

We use a feature-branching workflow. **Direct pushes to the `master` branch are disabled** or highly discouraged. To contribute:

1. **Fork** the repository (or if you have write access, create a new branch).
2. Create a branch for your feature or bug fix:
   - For features: `feat/mi-nueva-funcion`
   - For bug fixes: `fix/error-al-cargar`
   - For chores/documentation: `chore/actualizar-readme`
3. Develop your changes.
4. **Push** your branch and open a **Pull Request** against the `master` branch.
5. Our CI workflow will automatically run formatting checks and tests.
6. Once approved, it will be merged into `master`.

## ✍️ Commit Message Convention

To automate our semantic versioning and release notes (changelog) generation, we strictly follow **[Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)**.

Every commit message **must** be formatted as follows:

```
<type>(<optional scope>): <description>

[optional body]

[optional footer(s)]
```

### Types

The `<type>` must be one of the following:

* **`feat`**: A new feature (correlates with a `MINOR` release).
* **`fix`**: A bug fix (correlates with a `PATCH` release).
* **`docs`**: Documentation only changes.
* **`style`**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc).
* **`refactor`**: A code change that neither fixes a bug nor adds a feature.
* **`perf`**: A code change that improves performance.
* **`test`**: Adding missing tests or correcting existing tests.
* **`chore`**: Changes to the build process or auxiliary tools and libraries such as documentation generation.

### Examples

* `feat: añadir nueva interfaz de selección de clase`
* `fix(ui): resolver NullPointerException al abrir la ventana modal`
* `docs: actualizar el README con instrucciones de setup`
* `chore: actualizar librería kotlin a la versión 2.1.0`

### Breaking Changes

If your commit introduces a breaking change, prepend a `!` before the colon or add a `BREAKING CHANGE:` footer. This will trigger a `MAJOR` release.
**Example**: `feat!: remove support for JetBrains IDE versions prior to 2023.1`

## 🚀 Release Process (Maintainers Only)

The release process is entirely automated through GitHub Actions!

We do **not** publish releases automatically on every merge. Instead, we bundle features and triggers the publication manually:

1. Go to the **Actions** tab in GitHub.
2. Select **Generate Release & Publish**.
3. Click **Run workflow** mapping to `master`.

The workflow will automatically:
1. Examine all conventional commits since the last release.
2. Calculate the next semantic version (`MAJOR`, `MINOR`, or `PATCH`).
3. Generate comprehensive Release Notes based on the commit history.
4. Convert those notes into standard HTML for JetBrains Marketplace.
5. Compile and publish the plugin.
6. Create a GitHub Release and a git tag.

