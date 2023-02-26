plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("io.github.wjf510.maven.publish").version("1.0.5")
}

android {
    namespace = "com.zero.fingerprint.detector"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

val sonatypeGroupId = "io.github.wjf510"

mavenPublish {
    mavenReleaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"

    publishGroupId.set("${sonatypeGroupId}.fingerprint.change")

    publishVersion.set("1.0.1")

    pom.set {
        //组件的基本信息
        name.set("FingerprintChangeDetector")
        description.set("FingerprintChangeDetector")
        url.set("https://github.com/weijunfeng/FingerprintChangeDetector")
        //licenses文件
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        //开发者信息
        developers {
            developer {
                id.set("weijunfeng")
                name.set("weijunfeng")
                email.set("891130789@qq.com")
            }
        }
        //版本控制仓库地址
        scm {
            url.set("https://github.com/weijunfeng/FingerprintChangeDetector")
        }
    }
}